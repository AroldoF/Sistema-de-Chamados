package com.edu.chamados.departments.services;

import com.edu.chamados.departments.dtos.*;
import com.edu.chamados.departments.entities.Category;
import com.edu.chamados.departments.entities.Department;
import com.edu.chamados.departments.repositories.CategoryRepository;
import com.edu.chamados.departments.repositories.DepartmentRepository;
import com.edu.chamados.shared.exceptions.BusinessRuleException;
import com.edu.chamados.shared.exceptions.ResourceNotFoundException;
import com.edu.chamados.shared.web.PageResponse;
import com.edu.chamados.tickets.repositories.TicketRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final CategoryRepository categoryRepository;
    private final TicketRepository ticketRepository;

    public DepartmentService(DepartmentRepository departmentRepository, CategoryRepository categoryRepository,
                             TicketRepository ticketRepository) {
        this.departmentRepository = departmentRepository;
        this.categoryRepository = categoryRepository;
        this.ticketRepository = ticketRepository;
    }

    public DepartmentResponse createDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByCodeIgnoreCase(request.code())) throw new BusinessRuleException("A sigla já está em uso.");
        Department department = new Department();
        apply(department, request);
        return toResponse(departmentRepository.save(department));
    }

    public PageResponse<DepartmentResponse> listDepartments(String name, Pageable pageable) {
        Specification<Department> spec = (root, query, cb) -> name == null || name.isBlank() ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        return page(departmentRepository.findAll(spec, pageable), DepartmentService::toResponse);
    }

    public DepartmentResponse getDepartment(Long id) { return toResponse(findDepartment(id)); }

    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department department = findDepartment(id);
        if (departmentRepository.existsByCodeIgnoreCaseAndIdNot(request.code(), id)) throw new BusinessRuleException("A sigla já está em uso.");
        apply(department, request);
        return toResponse(department);
    }

    public void deleteDepartment(Long id) {
        Department department = findDepartment(id);
        if (ticketRepository.existsByCategoryDepartmentId(id)) throw new BusinessRuleException("Não é permitido excluir setor com chamados vinculados.");
        if (!department.getCategories().isEmpty()) throw new BusinessRuleException("Não é permitido excluir setor que possui categorias.");
        departmentRepository.delete(department);
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category(); apply(category, request);
        return toResponse(categoryRepository.save(category));
    }

    public PageResponse<CategoryResponse> listCategories(String name, Long departmentId, Pageable pageable) {
        Specification<Category> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();
            if (name != null && !name.isBlank()) predicates.getExpressions().add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            if (departmentId != null) predicates.getExpressions().add(cb.equal(root.get("department").get("id"), departmentId));
            return predicates;
        };
        return page(categoryRepository.findAll(spec, pageable), DepartmentService::toResponse);
    }

    public PageResponse<CategoryResponse> listCategoriesByDepartment(Long departmentId, Pageable pageable) {
        findDepartment(departmentId);
        return listCategories(null, departmentId, pageable);
    }

    public CategoryResponse getCategory(Long id) { return toResponse(findCategory(id)); }

    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = findCategory(id); apply(category, request); return toResponse(category);
    }

    public void deleteCategory(Long id) {
        Category category = findCategory(id);
        if (ticketRepository.existsByCategoryId(id)) throw new BusinessRuleException("Não é permitido excluir categoria com chamados vinculados.");
        categoryRepository.delete(category);
    }

    private void apply(Department target, DepartmentRequest source) { target.setName(source.name()); target.setCode(source.code().toUpperCase()); }
    private void apply(Category target, CategoryRequest source) { target.setName(source.name()); target.setDescription(source.description()); target.setDepartment(findDepartment(source.departmentId())); }
    private Department findDepartment(Long id) { return departmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Setor não encontrado.")); }
    private Category findCategory(Long id) { return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada.")); }
    private static DepartmentResponse toResponse(Department d) { return new DepartmentResponse(d.getId(), d.getName(), d.getCode()); }
    private static CategoryResponse toResponse(Category c) { return new CategoryResponse(c.getId(), c.getName(), c.getDescription(), c.getDepartment().getId(), c.getDepartment().getName()); }
    private static <T, R> PageResponse<R> page(Page<T> page, java.util.function.Function<T, R> mapper) { return PageResponse.from(page.map(mapper)); }
}
