package com.edu.chamados.users.dtos;

import com.edu.chamados.users.entities.UserType;

public record UserResponse(Long id, String name, String email, String registration, UserType type, Long departmentId, String departmentName) { }
