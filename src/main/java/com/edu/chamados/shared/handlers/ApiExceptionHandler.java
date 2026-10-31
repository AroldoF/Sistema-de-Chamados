package com.edu.chamados.shared.handlers;

import com.edu.chamados.shared.exceptions.BusinessRuleException;
import com.edu.chamados.shared.exceptions.ResourceNotFoundException;
import com.edu.chamados.shared.web.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ApiError> notFound(ResourceNotFoundException ex, HttpServletRequest request) { return error(HttpStatus.NOT_FOUND, ex.getMessage(), request); }
    @ExceptionHandler(BusinessRuleException.class)
    ResponseEntity<ApiError> conflict(BusinessRuleException ex, HttpServletRequest request) { return error(HttpStatus.CONFLICT, ex.getMessage(), request); }
    @ExceptionHandler({MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<ApiError> badRequest(Exception ex, HttpServletRequest request) {
        String message = ex instanceof MethodArgumentNotValidException invalid ? invalid.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining("; ")) : "Parâmetro de requisição inválido.";
        return error(HttpStatus.BAD_REQUEST, message, request);
    }
    private ResponseEntity<ApiError> error(HttpStatus status, String message, HttpServletRequest request) { return ResponseEntity.status(status).body(new ApiError(LocalDateTime.now(), status.value(), message, request.getRequestURI())); }
}
