package com.edu.chamados.tickets.dtos;
import jakarta.validation.constraints.NotNull;
public record CancelTicketRequest(@NotNull Long requesterId) { }
