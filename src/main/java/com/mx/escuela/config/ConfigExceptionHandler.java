package com.mx.escuela.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class ConfigExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
        "timestamp", LocalDateTime.now().toString(),
        "status", 404,
        "error", "Not Found",
        "message", ex.getMessage()
    ));
  }

  @ExceptionHandler({ MethodArgumentNotValidException.class, BindException.class })
  public ResponseEntity<?> handleValidation(Exception ex) {
    return ResponseEntity.badRequest().body(Map.of(
        "timestamp", LocalDateTime.now().toString(),
        "status", 400,
        "error", "Bad Request",
        "message", "Datos inválidos",
        "detail", ex.getMessage()
    ));
  }
}
