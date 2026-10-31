package com.edu.chamados.users.services;

import com.edu.chamados.departments.entities.Department;
import com.edu.chamados.departments.repositories.DepartmentRepository;
import com.edu.chamados.shared.exceptions.BusinessRuleException;
import com.edu.chamados.shared.exceptions.ResourceNotFoundException;
import com.edu.chamados.shared.web.PageResponse;
import com.edu.chamados.users.dtos.UserRequest;
import com.edu.chamados.users.dtos.UserResponse;
import com.edu.chamados.users.entities.UserAccount;
import com.edu.chamados.users.entities.UserType;
import com.edu.chamados.users.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    public UserService(UserRepository userRepository, DepartmentRepository departmentRepository) { this.userRepository = userRepository; this.departmentRepository = departmentRepository; }

    public UserResponse create(UserRequest request) {
        validateUnique(request, null);
        UserAccount user = new UserAccount(); apply(user, request); return toResponse(userRepository.save(user));
    }
    public PageResponse<UserResponse> list(String name, UserType type, Long departmentId, Pageable pageable) {
        Specification<UserAccount> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();
            if (name != null && !name.isBlank()) predicates.getExpressions().add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            if (type != null) predicates.getExpressions().add(cb.equal(root.get("type"), type));
            if (departmentId != null) predicates.getExpressions().add(cb.equal(root.get("department").get("id"), departmentId));
            return predicates;
        };
        return PageResponse.from(userRepository.findAll(spec, pageable).map(UserService::toResponse));
    }
    public UserResponse get(Long id) { return toResponse(find(id)); }
    public UserResponse update(Long id, UserRequest request) { UserAccount user = find(id); validateUnique(request, id); apply(user, request); return toResponse(user); }

    public UserAccount find(Long id) { return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado.")); }
    private void validateUnique(UserRequest request, Long id) {
        boolean emailUsed = id == null ? userRepository.existsByEmailIgnoreCase(request.email()) : userRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), id);
        boolean registrationUsed = id == null ? userRepository.existsByRegistration(request.registration()) : userRepository.existsByRegistrationAndIdNot(request.registration(), id);
        if (emailUsed) throw new BusinessRuleException("O e-mail já está em uso.");
        if (registrationUsed) throw new BusinessRuleException("A matrícula já está em uso.");
    }
    private void apply(UserAccount target, UserRequest source) {
        target.setName(source.name()); target.setEmail(source.email().toLowerCase()); target.setRegistration(source.registration()); target.setType(source.type());
        if (source.type() == UserType.TECNICO) {
            if (source.departmentId() == null) throw new BusinessRuleException("Técnico deve pertencer a um setor.");
            target.setDepartment(departmentRepository.findById(source.departmentId()).orElseThrow(() -> new ResourceNotFoundException("Setor não encontrado.")));
        } else target.setDepartment(null);
    }
    private static UserResponse toResponse(UserAccount u) { return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRegistration(), u.getType(), u.getDepartment() == null ? null : u.getDepartment().getId(), u.getDepartment() == null ? null : u.getDepartment().getName()); }
}
