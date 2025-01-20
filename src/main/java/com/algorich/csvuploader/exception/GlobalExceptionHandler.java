package com.algorich.csvuploader.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CsvValidationException.class)
    public ResponseEntity<Map<String, Object>> handleCsvValidationException(CsvValidationException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of(
                    "message", "CSV validation failed",
                    "errors", ex.getErrors()
                ));
    }
} 