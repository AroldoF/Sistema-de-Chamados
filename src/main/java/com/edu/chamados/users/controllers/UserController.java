package com.edu.chamados.users.controllers;

import com.edu.chamados.shared.web.PageResponse;
import com.edu.chamados.users.dtos.UserRequest;
import com.edu.chamados.users.dtos.UserResponse;
import com.edu.chamados.users.entities.UserType;
import com.edu.chamados.users.services.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UserController {
    private final UserService service;
    public UserController(UserService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public UserResponse create(@RequestBody @Valid UserRequest request) { return service.create(request); }
    @GetMapping public PageResponse<UserResponse> list(@RequestParam(required = false) String nome, @RequestParam(required = false) UserType tipo, @RequestParam(required = false) Long setorId, @PageableDefault Pageable pageable) { return service.list(nome, tipo, setorId, pageable); }
    @GetMapping("/{id}") public UserResponse get(@PathVariable Long id) { return service.get(id); }
    @PutMapping("/{id}") public UserResponse update(@PathVariable Long id, @RequestBody @Valid UserRequest request) { return service.update(id, request); }
}
