package com.algorich.csvuploader.validation;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CsvValidator {
    private static final String[] EXPECTED_HEADERS = {
            "source", "codeListCode", "code", "displayValue", 
            "longDescription", "fromDate", "toDate", "sortingPriority"
    };
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public ValidationResult validate(MultipartFile file) {
        List<String> errors = new ArrayList<>();

        if (file == null || file.isEmpty()) {
            errors.add("File is empty");
            return new ValidationResult(false, errors);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            // Validate headers
            String headerLine = reader.readLine();
            if (headerLine == null) {
                errors.add("CSV file is empty");
                return new ValidationResult(false, errors);
            }

            String[] headers = headerLine.split(",");
            if (!validateHeaders(headers)) {
                errors.add("Invalid CSV headers. Expected: " + String.join(", ", EXPECTED_HEADERS));
                return new ValidationResult(false, errors);
            }

            // Validate data rows
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                
                if (values.length != EXPECTED_HEADERS.length) {
                    errors.add("Line " + lineNumber + ": Invalid number of columns");
                    continue;
                }

                // Validate dates
                String fromDate = values[5].replace("\"", "");
                String toDate = values[6].replace("\"", "");

                if (!fromDate.isEmpty()) {
                    try {
                        LocalDate.parse(fromDate, dateFormatter);
                    } catch (DateTimeParseException e) {
                        errors.add("Line " + lineNumber + ": Invalid fromDate format. Expected dd-MM-yyyy");
                    }
                }

                if (!toDate.isEmpty()) {
                    try {
                        LocalDate.parse(toDate, dateFormatter);
                    } catch (DateTimeParseException e) {
                        errors.add("Line " + lineNumber + ": Invalid toDate format. Expected dd-MM-yyyy");
                    }
                }

                // Validate required fields
                if (values[0].replace("\"", "").isEmpty()) {
                    errors.add("Line " + lineNumber + ": Source is required");
                }
                if (values[2].replace("\"", "").isEmpty()) {
                    errors.add("Line " + lineNumber + ": Code is required");
                }
            }

        } catch (IOException e) {
            errors.add("Error reading CSV file: " + e.getMessage());
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    private boolean validateHeaders(String[] headers) {
        return Arrays.equals(
            Arrays.stream(headers)
                .map(h -> h.replace("\"", "").trim())
                .toArray(String[]::new),
            EXPECTED_HEADERS
        );
    }
} 