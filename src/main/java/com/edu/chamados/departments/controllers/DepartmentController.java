package com.edu.chamados.departments.controllers;

import com.edu.chamados.departments.dtos.*;
import com.edu.chamados.departments.services.DepartmentService;
import com.edu.chamados.shared.web.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class DepartmentController {
    private final DepartmentService service;
    public DepartmentController(DepartmentService service) { this.service = service; }

    @PostMapping("/setores") @ResponseStatus(HttpStatus.CREATED)
    public DepartmentResponse create(@RequestBody @Valid DepartmentRequest request) { return service.createDepartment(request); }
    @GetMapping("/setores")
    public PageResponse<DepartmentResponse> list(@RequestParam(required = false) String nome, @PageableDefault Pageable pageable) { return service.listDepartments(nome, pageable); }
    @GetMapping("/setores/{id}") public DepartmentResponse get(@PathVariable Long id) { return service.getDepartment(id); }
    @PutMapping("/setores/{id}") public DepartmentResponse update(@PathVariable Long id, @RequestBody @Valid DepartmentRequest request) { return service.updateDepartment(id, request); }
    @DeleteMapping("/setores/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) { service.deleteDepartment(id); }

    @PostMapping("/categorias") @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(@RequestBody @Valid CategoryRequest request) { return service.createCategory(request); }
    @GetMapping("/categorias") public PageResponse<CategoryResponse> listCategories(@RequestParam(required = false) String nome, @RequestParam(required = false) Long setorId, @PageableDefault Pageable pageable) { return service.listCategories(nome, setorId, pageable); }
    @GetMapping("/categorias/{id}") public CategoryResponse getCategory(@PathVariable Long id) { return service.getCategory(id); }
    @PutMapping("/categorias/{id}") public CategoryResponse updateCategory(@PathVariable Long id, @RequestBody @Valid CategoryRequest request) { return service.updateCategory(id, request); }
    @DeleteMapping("/categorias/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteCategory(@PathVariable Long id) { service.deleteCategory(id); }
    @GetMapping("/setores/{id}/categorias") public PageResponse<CategoryResponse> categoriesByDepartment(@PathVariable Long id, @PageableDefault Pageable pageable) { return service.listCategoriesByDepartment(id, pageable); }
}
