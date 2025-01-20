package com.algorich.csvuploader.validation;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;

class CsvValidatorTest {

    private final CsvValidator validator = new CsvValidator();

    @Test
    void shouldValidateCorrectCsvFile() {
        String content = "source,codeListCode,code,displayValue,longDescription,fromDate,toDate,sortingPriority\n" +
                "\"ZIB\",\"ZIB001\",\"271636001\",\"Test\",\"Description\",\"01-01-2019\",\"\",\"1\"";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", content.getBytes());

        ValidationResult result = validator.validate(file);

        assertThat(result.isValid()).isTrue();
        assertThat(result.getErrors()).isEmpty();
    }

    @Test
    void shouldFailOnInvalidHeaders() {
        String content = "invalid,headers\nZIB,TEST";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", content.getBytes());

        ValidationResult result = validator.validate(file);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).anyMatch(error -> error.contains("Invalid CSV headers"));
    }

    @Test
    void shouldFailOnInvalidDateFormat() {
        String content = "source,codeListCode,code,displayValue,longDescription,fromDate,toDate,sortingPriority\n" +
                "\"ZIB\",\"ZIB001\",\"271636001\",\"Test\",\"Description\",\"2019-01-01\",\"\",\"1\"";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", content.getBytes());

        ValidationResult result = validator.validate(file);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).contains("Line 2: Invalid fromDate format. Expected dd-MM-yyyy");
    }

    @Test
    void shouldFailOnMissingRequiredFields() {
        String content = "source,codeListCode,code,displayValue,longDescription,fromDate,toDate,sortingPriority\n" +
                "\"\",\"ZIB001\",\"\",\"Test\",\"Description\",\"01-01-2019\",\"\",\"1\"";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", content.getBytes());

        ValidationResult result = validator.validate(file);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors())
            .contains("Line 2: Source is required")
            .contains("Line 2: Code is required");
    }
} 