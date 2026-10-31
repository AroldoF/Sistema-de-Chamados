package com.edu.chamados.departments.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepartmentRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 10) String code) { }
