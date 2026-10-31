package com.edu.chamados.tickets.services;

import com.edu.chamados.departments.entities.Category;
import com.edu.chamados.departments.repositories.CategoryRepository;
import com.edu.chamados.shared.exceptions.BusinessRuleException;
import com.edu.chamados.shared.exceptions.ResourceNotFoundException;
import com.edu.chamados.shared.web.PageResponse;
import com.edu.chamados.tickets.dtos.*;
import com.edu.chamados.tickets.entities.Interaction;
import com.edu.chamados.tickets.entities.Ticket;
import com.edu.chamados.tickets.enums.Priority;
import com.edu.chamados.tickets.enums.TicketStatus;
import com.edu.chamados.tickets.repositories.InteractionRepository;
import com.edu.chamados.tickets.repositories.TicketRepository;
import com.edu.chamados.users.entities.UserAccount;
import com.edu.chamados.users.entities.UserType;
import com.edu.chamados.users.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional
public class TicketService {
    private final TicketRepository ticketRepository;
    private final InteractionRepository interactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    public TicketService(TicketRepository ticketRepository, InteractionRepository interactionRepository, CategoryRepository categoryRepository, UserService userService) { this.ticketRepository = ticketRepository; this.interactionRepository = interactionRepository; this.categoryRepository = categoryRepository; this.userService = userService; }

    public TicketResponse create(TicketCreateRequest request) {
        UserAccount requester = userService.find(request.requesterId());
        if (requester.getType() != UserType.SOLICITANTE) throw new BusinessRuleException("Apenas solicitantes podem abrir chamados.");
        Ticket ticket = new Ticket(); ticket.setTitle(request.title()); ticket.setDescription(request.description()); ticket.setLocation(request.location());
        ticket.setPriority(request.priority() == null ? Priority.MEDIA : request.priority()); ticket.setStatus(TicketStatus.ABERTO); ticket.setOpeningDate(LocalDateTime.now());
        ticket.setRequester(requester); ticket.setCategory(findCategory(request.categoryId()));
        return toResponse(ticketRepository.save(ticket));
    }
    public PageResponse<TicketResponse> list(TicketStatus status, Priority priority, Long departmentId, Long categoryId, Long requesterId, Long technicianId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return PageResponse.from(ticketRepository.findAll(spec(status, priority, departmentId, categoryId, requesterId, technicianId, startDate, endDate), pageable).map(TicketService::toResponse));
    }
    public TicketDetailsResponse get(Long id) { Ticket ticket = find(id); return new TicketDetailsResponse(toResponse(ticket), ticket.getInteractions().stream().map(TicketService::toResponse).toList()); }
    public TicketResponse assume(Long id, AssumeTicketRequest request) {
        Ticket ticket = find(id); ensureOpen(ticket, "assumido"); UserAccount technician = userService.find(request.technicianId());
        if (technician.getType() != UserType.TECNICO) throw new BusinessRuleException("O usuário informado não é técnico.");
        if (technician.getDepartment() == null || !technician.getDepartment().getId().equals(ticket.getCategory().getDepartment().getId())) throw new BusinessRuleException("O técnico não pertence ao setor da categoria do chamado.");
        ticket.setTechnician(technician); ticket.setStatus(TicketStatus.EM_ATENDIMENTO); return toResponse(ticket);
    }
    public TicketResponse resolve(Long id, ResolveTicketRequest request) {
        Ticket ticket = find(id); ensureActive(ticket); ensureAssignedTechnician(ticket, request.technicianId());
        ticket.setSolution(request.solution()); ticket.setClosingDate(LocalDateTime.now()); ticket.setStatus(TicketStatus.RESOLVIDO); return toResponse(ticket);
    }
    public TicketResponse cancel(Long id, CancelTicketRequest request) {
        Ticket ticket = find(id); ensureOpen(ticket, "cancelado");
        if (!ticket.getRequester().getId().equals(request.requesterId())) throw new BusinessRuleException("Somente o solicitante do chamado pode cancelá-lo.");
        ticket.setStatus(TicketStatus.CANCELADO); return toResponse(ticket);
    }
    public InteractionResponse addInteraction(Long id, InteractionRequest request) {
        Ticket ticket = find(id); if (ticket.getStatus() == TicketStatus.RESOLVIDO || ticket.getStatus() == TicketStatus.CANCELADO) throw new BusinessRuleException("Chamados encerrados não aceitam novas interações.");
        UserAccount author = userService.find(request.authorId()); boolean requester = ticket.getRequester().getId().equals(author.getId()); boolean technician = ticket.getTechnician() != null && ticket.getTechnician().getId().equals(author.getId());
        if (!requester && !technician) throw new BusinessRuleException("Apenas o solicitante ou o técnico atribuído podem interagir no chamado.");
        Interaction interaction = new Interaction(); interaction.setTicket(ticket); interaction.setAuthor(author); interaction.setText(request.text()); interaction.setDateTime(LocalDateTime.now()); return toResponse(interactionRepository.save(interaction));
    }
    public PageResponse<TicketResponse> listByUser(Long userId, TicketStatus status, Pageable pageable) {
        UserAccount user = userService.find(userId);
        Specification<Ticket> spec = user.getType() == UserType.SOLICITANTE ? (root, query, cb) -> cb.equal(root.get("requester").get("id"), userId) : (root, query, cb) -> cb.equal(root.get("technician").get("id"), userId);
        if (status != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        return PageResponse.from(ticketRepository.findAll(spec, pageable).map(TicketService::toResponse));
    }
    private Specification<Ticket> spec(TicketStatus status, Priority priority, Long departmentId, Long categoryId, Long requesterId, Long technicianId, LocalDate startDate, LocalDate endDate) { return (root, query, cb) -> {
        var p = cb.conjunction();
        if (status != null) p.getExpressions().add(cb.equal(root.get("status"), status)); if (priority != null) p.getExpressions().add(cb.equal(root.get("priority"), priority));
        if (departmentId != null) p.getExpressions().add(cb.equal(root.get("category").get("department").get("id"), departmentId)); if (categoryId != null) p.getExpressions().add(cb.equal(root.get("category").get("id"), categoryId));
        if (requesterId != null) p.getExpressions().add(cb.equal(root.get("requester").get("id"), requesterId)); if (technicianId != null) p.getExpressions().add(cb.equal(root.get("technician").get("id"), technicianId));
        if (startDate != null) p.getExpressions().add(cb.greaterThanOrEqualTo(root.get("openingDate"), startDate.atStartOfDay())); if (endDate != null) p.getExpressions().add(cb.lessThan(root.get("openingDate"), endDate.plusDays(1).atStartOfDay())); return p; }; }
    private Ticket find(Long id) { return ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Chamado não encontrado.")); }
    private Category findCategory(Long id) { return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada.")); }
    private void ensureOpen(Ticket ticket, String action) { if (ticket.getStatus() != TicketStatus.ABERTO) throw new BusinessRuleException("Apenas chamados abertos podem ser " + action + "s."); }
    private void ensureActive(Ticket ticket) { if (ticket.getStatus() != TicketStatus.EM_ATENDIMENTO) throw new BusinessRuleException("Apenas chamados em atendimento podem ser resolvidos."); }
    private void ensureAssignedTechnician(Ticket ticket, Long technicianId) { if (ticket.getTechnician() == null || !ticket.getTechnician().getId().equals(technicianId)) throw new BusinessRuleException("Somente o técnico atribuído pode resolver o chamado."); }
    private static TicketResponse toResponse(Ticket t) { return new TicketResponse(t.getId(), t.getTitle(), t.getDescription(), t.getLocation(), t.getPriority(), t.getStatus(), t.getOpeningDate(), t.getClosingDate(), t.getSolution(), t.getRequester().getId(), t.getRequester().getName(), t.getCategory().getId(), t.getCategory().getName(), t.getCategory().getDepartment().getId(), t.getTechnician() == null ? null : t.getTechnician().getId(), t.getTechnician() == null ? null : t.getTechnician().getName()); }
    private static InteractionResponse toResponse(Interaction i) { return new InteractionResponse(i.getId(), i.getText(), i.getDateTime(), i.getAuthor().getId(), i.getAuthor().getName()); }
}
