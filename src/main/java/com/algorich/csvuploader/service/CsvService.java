package com.algorich.csvuploader.service;

import com.algorich.csvuploader.model.CsvData;
import com.algorich.csvuploader.repository.CsvDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CsvService {

    private final CsvDataRepository repository;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public void uploadCsv(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean firstLine = true;
            List<CsvData> dataList = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                CsvData data = new CsvData();
                data.setSource(values[0].replace("\"", ""));
                data.setCodeListCode(values[1].replace("\"", ""));
                data.setCode(values[2].replace("\"", ""));
                data.setDisplayValue(values[3].replace("\"", ""));
                data.setLongDescription(values[4].replace("\"", ""));
                
                String fromDateStr = values[5].replace("\"", "");
                data.setFromDate(fromDateStr.isEmpty() ? null : LocalDate.parse(fromDateStr, dateFormatter));
                
                String toDateStr = values[6].replace("\"", "");
                data.setToDate(toDateStr.isEmpty() ? null : LocalDate.parse(toDateStr, dateFormatter));
                
                data.setSortingPriority(values[7].replace("\"", ""));
                dataList.add(data);
            }
            repository.saveAll(dataList);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process CSV file", e);
        }
    }

    public Optional<CsvData> getByCode(String code) {
        return repository.findByCode(code);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public List<CsvData> getAll() {
        return repository.findAll();
    }

    public String generateCsv() {
        List<CsvData> allData = repository.findAll();
        StringWriter writer = new StringWriter();
        
        // Write header
        writer.write("\"source\",\"codeListCode\",\"code\",\"displayValue\",\"longDescription\",\"fromDate\",\"toDate\",\"sortingPriority\"\n");
        
        // Write data
        allData.forEach(data -> {
            writer.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                data.getSource(),
                data.getCodeListCode(),
                data.getCode(),
                data.getDisplayValue(),
                data.getLongDescription(),
                data.getFromDate() != null ? data.getFromDate().format(dateFormatter) : "",
                data.getToDate() != null ? data.getToDate().format(dateFormatter) : "",
                data.getSortingPriority()));
        });
        
        return writer.toString();
    }
} 