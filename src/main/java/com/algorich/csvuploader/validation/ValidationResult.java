package com.algorich.csvuploader.validation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ValidationResult {
    private boolean valid;
    private List<String> errors;
} 