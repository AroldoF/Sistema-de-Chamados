package com.edu.chamados.tickets.dtos;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public record InteractionRequest(@NotBlank @Size(max = 10000) String text, @NotNull Long authorId) { }
