package com.edu.chamados.tickets.controllers;

import com.edu.chamados.shared.web.PageResponse;
import com.edu.chamados.tickets.dtos.*;
import com.edu.chamados.tickets.enums.Priority;
import com.edu.chamados.tickets.enums.TicketStatus;
import com.edu.chamados.tickets.services.TicketService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
public class TicketController {
    private final TicketService service;
    public TicketController(TicketService service) { this.service = service; }
    @PostMapping("/chamados") @ResponseStatus(HttpStatus.CREATED) public TicketResponse create(@RequestBody @Valid TicketCreateRequest request) { return service.create(request); }
    @GetMapping("/chamados") public PageResponse<TicketResponse> list(@RequestParam(required = false) TicketStatus status, @RequestParam(required = false) Priority prioridade, @RequestParam(required = false) Long setorId, @RequestParam(required = false) Long categoriaId, @RequestParam(required = false) Long solicitanteId, @RequestParam(required = false) Long tecnicoId, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim, @PageableDefault(sort = "openingDate") Pageable pageable) { return service.list(status, prioridade, setorId, categoriaId, solicitanteId, tecnicoId, dataInicio, dataFim, ticketPageable(pageable)); }
    @GetMapping("/chamados/{id}") public TicketDetailsResponse get(@PathVariable Long id) { return service.get(id); }
    @PatchMapping("/chamados/{id}/assumir") public TicketResponse assume(@PathVariable Long id, @RequestBody @Valid AssumeTicketRequest request) { return service.assume(id, request); }
    @PatchMapping("/chamados/{id}/resolver") public TicketResponse resolve(@PathVariable Long id, @RequestBody @Valid ResolveTicketRequest request) { return service.resolve(id, request); }
    @PatchMapping("/chamados/{id}/cancelar") public TicketResponse cancel(@PathVariable Long id, @RequestBody @Valid CancelTicketRequest request) { return service.cancel(id, request); }
    @PostMapping("/chamados/{id}/interacoes") @ResponseStatus(HttpStatus.CREATED) public InteractionResponse interaction(@PathVariable Long id, @RequestBody @Valid InteractionRequest request) { return service.addInteraction(id, request); }
    @GetMapping("/usuarios/{id}/chamados") public PageResponse<TicketResponse> listByUser(@PathVariable Long id, @RequestParam(required = false) TicketStatus status, @PageableDefault(sort = "openingDate") Pageable pageable) { return service.listByUser(id, status, ticketPageable(pageable)); }
    private Pageable ticketPageable(Pageable pageable) {
        List<Sort.Order> orders = pageable.getSort().stream()
                .map(order -> new Sort.Order(order.getDirection(), order.getProperty().equals("dataAbertura") ? "openingDate" : order.getProperty()))
                .toList();
        Sort sort = orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }
}
