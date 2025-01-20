package com.algorich.csvuploader.service;

import com.algorich.csvuploader.model.CsvData;
import com.algorich.csvuploader.repository.CsvDataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
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

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

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
        // Given
        String content = "source,codeListCode,code,displayValue,longDescription,fromDate,toDate,sortingPriority\n" +
                "\"ZIB\",\"ZIB001\",\"271636001\",\"Test\",\"Description\",\"01-01-2019\",\"\",\"1\"";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                content.getBytes()
        );

        ArgumentCaptor<List<CsvData>> dataListCaptor = ArgumentCaptor.forClass(List.class);

        // When
        service.uploadCsv(file);

        // Then
        verify(repository).saveAll(dataListCaptor.capture());
        List<CsvData> savedData = dataListCaptor.getValue();
        
        assertThat(savedData).hasSize(1);
        CsvData data = savedData.get(0);
        assertThat(data)
            .satisfies(d -> {
                assertThat(d.getSource()).isEqualTo("ZIB");
                assertThat(d.getCodeListCode()).isEqualTo("ZIB001");
                assertThat(d.getCode()).isEqualTo("271636001");
                assertThat(d.getDisplayValue()).isEqualTo("Test");
                assertThat(d.getLongDescription()).isEqualTo("Description");
                assertThat(d.getFromDate()).isEqualTo(LocalDate.parse("01-01-2019", dateFormatter));
                assertThat(d.getToDate()).isNull();
                assertThat(d.getSortingPriority()).isEqualTo("1");
            });
    }

    @Test
    void generateCsv_ShouldReturnFormattedString() {
        CsvData data = new CsvData();
        data.setSource("ZIB");
        data.setCode("TEST");
        data.setFromDate(LocalDate.parse("01-01-2019", dateFormatter));
        when(repository.findAll()).thenReturn(Arrays.asList(data));

        String result = service.generateCsv();

        assertThat(result).contains("ZIB");
        assertThat(result).contains("TEST");
        assertThat(result).contains("01-01-2019");
    }

    @Test
    void deleteAll_ShouldCallRepository() {
        service.deleteAll();
        verify(repository).deleteAll();
    }
} 