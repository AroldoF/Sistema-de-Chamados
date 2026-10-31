package com.edu.chamados.tickets.dtos;
import java.time.LocalDateTime;
public record InteractionResponse(Long id, String text, LocalDateTime dateTime, Long authorId, String authorName) { }
