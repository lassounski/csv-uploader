package com.algorich.csvuploader.service;

import com.algorich.csvuploader.model.CsvData;
import com.algorich.csvuploader.repository.CsvDataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CsvServiceTest {

    @Mock
    private CsvDataRepository repository;

    @InjectMocks
    private CsvService service;

    @Test
    void getByCode_ShouldReturnData() {
        CsvData data = new CsvData();
        data.setCode("testCode");
        when(repository.findByCode("testCode")).thenReturn(Optional.of(data));

        Optional<CsvData> result = service.getByCode("testCode");

        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("testCode");
    }

    @Test
    void uploadCsv_ShouldProcessAndSaveData() {
        String content = "source,codeListCode,code,displayValue,longDescription,fromDate,toDate,sortingPriority\n" +
                "\"ZIB\",\"ZIB001\",\"271636001\",\"Test\",\"Description\",\"01-01-2019\",\"\",\"1\"";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                content.getBytes()
        );

        service.uploadCsv(file);

        verify(repository).saveAll(any());
    }

    @Test
    void deleteAll_ShouldCallRepository() {
        service.deleteAll();
        verify(repository).deleteAll();
    }

    @Test
    void generateCsv_ShouldReturnFormattedString() {
        CsvData data = new CsvData();
        data.setSource("ZIB");
        data.setCode("TEST");
        when(repository.findAll()).thenReturn(Arrays.asList(data));

        String result = service.generateCsv();

        assertThat(result).contains("ZIB");
        assertThat(result).contains("TEST");
    }
} 