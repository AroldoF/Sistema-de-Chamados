package com.edu.chamados.tickets.dtos;
import java.util.List;
public record TicketDetailsResponse(TicketResponse ticket, List<InteractionResponse> interactions) { }
