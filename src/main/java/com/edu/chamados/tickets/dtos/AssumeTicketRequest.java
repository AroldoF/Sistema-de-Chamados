package com.edu.chamados.tickets.dtos;
import jakarta.validation.constraints.NotNull;
public record AssumeTicketRequest(@NotNull Long technicianId) { }
