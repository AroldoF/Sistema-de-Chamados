package com.edu.chamados.tickets.dtos;
import com.edu.chamados.tickets.enums.Priority;
import com.edu.chamados.tickets.enums.TicketStatus;
import java.time.LocalDateTime;
public record TicketResponse(Long id, String title, String description, String location, Priority priority, TicketStatus status,
                             LocalDateTime openingDate, LocalDateTime closingDate, String solution, Long requesterId,
                             String requesterName, Long categoryId, String categoryName, Long departmentId, Long technicianId, String technicianName) { }
