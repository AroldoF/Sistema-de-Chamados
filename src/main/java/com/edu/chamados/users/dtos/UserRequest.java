package com.edu.chamados.users.dtos;

import com.edu.chamados.users.entities.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest(@NotBlank @Size(max = 255) String name,
                          @NotBlank @Email @Size(max = 255) String email,
                          @NotBlank @Size(max = 40) String registration,
                          @NotNull UserType type, Long departmentId) { }
