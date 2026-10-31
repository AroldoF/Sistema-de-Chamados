package com.edu.chamados.tickets.dtos;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public record ResolveTicketRequest(@NotNull Long technicianId, @NotBlank @Size(max = 10000) String solution) { }
