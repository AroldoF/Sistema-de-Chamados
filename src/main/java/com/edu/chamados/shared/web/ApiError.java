package com.edu.chamados.shared.web;
import java.time.LocalDateTime;
public record ApiError(LocalDateTime timestamp, int status, String message, String path) { }
