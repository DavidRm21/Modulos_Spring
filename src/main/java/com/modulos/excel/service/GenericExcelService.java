package com.modulos.excel.service;


import com.modulos.excel.exception.ReportGenerationException;
import com.modulos.excel.interfaces.ExcelColumn;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generic Excel report generator service
 * @param <T> Type of entity to generate report from
 */
@Service
@Slf4j
public class GenericExcelService<T> {

    /**
     * Generates Excel report for any list of entities
     * @param data List of entities to include in report
     * @param entityClass Class of the entities
     * @param sheetName Name for the Excel sheet
     * @return byte array containing the Excel file
     */
    public byte[] generateReport(List<T> data, Class<T> entityClass, String sheetName) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            createReport(outputStream, data, entityClass, sheetName);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Error generating report: ", e);
            throw new ReportGenerationException("Failed to generate report", e);
        }
    }

    private void createReport(OutputStream outputStream, List<T> data, Class<T> entityClass, String sheetName) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);

            // Get fields marked with @ExcelColumn
            List<Field> excelFields = getExcelFields(entityClass);

            // Configure columns
            configureColumns(sheet, excelFields);

            // Create header
            createHeaderRow(workbook, sheet, excelFields);

            // Populate data
            populateData(workbook, sheet, data, excelFields);

            workbook.write(outputStream);
        }
    }

    private List<Field> getExcelFields(Class<?> entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ExcelColumn.class))
                .sorted(Comparator.comparingInt(field ->
                        field.getAnnotation(ExcelColumn.class).order()))
                .collect(Collectors.toList());
    }

    private void configureColumns(Sheet sheet, List<Field> fields) {
        for (int i = 0; i < fields.size(); i++) {
            ExcelColumn annotation = fields.get(i).getAnnotation(ExcelColumn.class);
            sheet.setColumnWidth(i, annotation.width());
        }
    }

    private void createHeaderRow(XSSFWorkbook workbook, Sheet sheet, List<Field> fields) {
        CellStyle headerStyle = createHeaderStyle(workbook);
        Row header = sheet.createRow(0);

        for (int i = 0; i < fields.size(); i++) {
            ExcelColumn annotation = fields.get(i).getAnnotation(ExcelColumn.class);
            String headerText = annotation.header().isEmpty() ?
                    fields.get(i).getName() :
                    annotation.header();
            createHeaderCell(header, i, headerText, headerStyle);
        }
    }

    private CellStyle createHeaderStyle(XSSFWorkbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short)12);
        font.setBold(true);
        headerStyle.setFont(font);

        return headerStyle;
    }

    private void createHeaderCell(Row header, int column, String value, CellStyle style) {
        Cell cell = header.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void populateData(XSSFWorkbook workbook, Sheet sheet, List<T> data, List<Field> fields) {
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);

        int rowNum = 1;
        for (T entity : data) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                field.setAccessible(true);
                try {
                    Object value = field.get(entity);
                    createDataCell(row, i, value, style);
                } catch (IllegalAccessException e) {
                    log.error("Error accessing field: " + field.getName(), e);
                }
            }
        }
    }

    private void createDataCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        setCellValue(cell, value);
        cell.setCellStyle(style);
    }

    private void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof LocalDate) {
            cell.setCellValue(((LocalDate) value).format(DateTimeFormatter.ISO_DATE));
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue(((LocalDateTime) value).format(DateTimeFormatter.ISO_DATE_TIME));
        } else {
            cell.setCellValue(value.toString());
        }
    }
}