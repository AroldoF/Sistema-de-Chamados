package com.edu.chamados.tickets.dtos;

import com.edu.chamados.tickets.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TicketCreateRequest(@NotBlank @Size(max = 255) String title,
                                  @NotBlank @Size(max = 10000) String description,
                                  @NotBlank @Size(max = 255) String location,
                                  Priority priority, @NotNull Long requesterId, @NotNull Long categoryId) { }
