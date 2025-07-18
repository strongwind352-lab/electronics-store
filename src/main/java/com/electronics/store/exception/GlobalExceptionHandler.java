package com.electronics.store.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<Object> handleProductNotFoundException(
      ProductNotFoundException ex, WebRequest request) {
    Map<String, Object> body =
        createErrorBody(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        // Collect all validation errors into a single string for clarity.
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        Map<String, Object> body = createErrorBody(HttpStatus.BAD_REQUEST, "Validation Error", errors, request);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    private Map<String, Object> createErrorBody(HttpStatus status, String error, String message, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("path", request.getDescription(false));
        return body;
    }
}
