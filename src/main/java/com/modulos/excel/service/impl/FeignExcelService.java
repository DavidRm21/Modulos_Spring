package com.modulos.excel.service.impl;

import com.modulos.excel.entity.CustomerEntity;
import com.modulos.excel.exception.ReportGenerationException;
import com.modulos.excel.repository.CustomerRepository;
import com.modulos.excel.service.FeignReportService;
import com.modulos.excel.service.GenericExcelService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

/**
 * Service implementation for generating Excel reports and sending them to storage
 */
@Service
@Slf4j
@AllArgsConstructor
public class FeignExcelService implements FeignReportService {

    private final CustomerRepository repository;
    private final GenericExcelService<CustomerEntity> excelService;
//    private final StorageServiceClient storageClient;


    /**
     * Generates and uploads Excel report to storage service
     * @return String URL or identifier of stored file
     */
    @Override
    public String generateAndUploadReport() {
        try {
            // Create Excel in memory
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            createReport(outputStream);

            // Convert to MultipartFile
            MultipartFile multipartFile = convertToMultipartFile(
                    outputStream.toByteArray(),
                    String.format("Sales-%s.xlsx", LocalDate.now().getMonth())
            );

            // Upload to storage service
//            String result = storageClient.uploadFile(multipartFile);

            return "Se ha enviado con exito: " + multipartFile;
//            return result;

        } catch (IOException e) {
            log.error("Error generating or uploading report: ", e);
            throw new ReportGenerationException("Failed to generate or upload report", e);
        }
    }

    @Override
    public byte[] generateAndUploadReportBytes() {

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Generate Excel file in memory
            createReportBytes(outputStream);

            String filename = String.format("Sales-%s.xlsx", LocalDate.now().getMonth());
            String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

            // Upload bytes directly
            /*return storageClient.uploadFile(
                    filename,
                    contentType,
                    outputStream.toByteArray()
            );*/
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Error generating or uploading report: ", e);
            throw new ReportGenerationException("Failed to generate or upload report", e);
        }
    }

    @Override
    public byte[] generateCustomerReport() {
        List<CustomerEntity> customers = repository.findAll();
        return excelService.generateReport(
                customers,
                CustomerEntity.class,
                "Reporte de Clientes"
        );
    }

    /**
     * Creates Excel report and writes to provided OutputStream
     * @param outputStream Stream to write Excel data to
     */
    private void createReportBytes(OutputStream outputStream) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            var sheet = workbook.createSheet(SHEET_NAME);

            configureColumnsBytes(sheet);
            createHeaderRowBytes(workbook, sheet);
            populateDataBytes(workbook, sheet);

            workbook.write(outputStream);
        }
    }

    /**
     * Configures column widths
     */
    private void configureColumnsBytes(Sheet sheet) {
        sheet.setColumnWidth(0, 5000);
        sheet.setColumnWidth(1, 7000);
        sheet.setColumnWidth(2, 3000);
    }

    /**
     * Creates and styles the header row
     */
    private void createHeaderRowBytes(XSSFWorkbook workbook, Sheet sheet) {
        CellStyle headerStyle = createHeaderStyleBytes(workbook);
        Row header = sheet.createRow(0);

        createHeaderCellBytes(header, 0, COLUMN_CUSTOMER_ID, headerStyle);
        createHeaderCellBytes(header, 1, COLUMN_CUSTOMER_NAME, headerStyle);
        createHeaderCellBytes(header, 2, COLUMN_CUSTOMER_PURCHASES, headerStyle);
    }

    /**
     * Creates the style for header cells
     */
    private CellStyle createHeaderStyleBytes(XSSFWorkbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = workbook.createFont();
        font.setFontName(FONT_TYPE);
        font.setFontHeightInPoints((short)16);
        font.setBold(true);
        headerStyle.setFont(font);

        return headerStyle;
    }

    /**
     * Creates a header cell with the given style
     */
    private void createHeaderCellBytes(Row header, int column, String value, CellStyle style) {
        Cell cell = header.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    /**
     * Populates the sheet with customer data
     */
    private void populateDataBytes(XSSFWorkbook workbook, Sheet sheet) {
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);

        List<CustomerEntity> customers = repository.findAll();
        int rowPos = 1;

        for (CustomerEntity customer : customers) {
            Row row = sheet.createRow(rowPos++);
            createDataCellBytes(row, 0, customer.getId().toString(), style);
            createDataCellBytes(row, 1, customer.getName(), style);
            createDataCellBytes(row, 2, String.valueOf(getTotalPurchase(customer)), style);
        }
    }

    /**
     * Creates a data cell with the given style
     */
    private void createDataCellBytes(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    /**
     * Creates Excel report and writes to provided OutputStream
     * @param outputStream Stream to write Excel data to
     */
    private void createReport(OutputStream outputStream) throws IOException {
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet(SHEET_NAME);

        // Set column widths
        sheet.setColumnWidth(0, 5000);
        sheet.setColumnWidth(1, 7000);
        sheet.setColumnWidth(2, 3000);

        // Create and style header row
        createHeaderRow(workbook, sheet);

        // Add data rows
        populateData(workbook, sheet);

        // Write to output stream
        workbook.write(outputStream);
        workbook.close();
    }

    /**
     * Creates and styles the header row of the Excel sheet
     */
    private void createHeaderRow(XSSFWorkbook workbook, Sheet sheet) {
        var header = sheet.createRow(0);
        var headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        var font = workbook.createFont();
        font.setFontName(FONT_TYPE);
        font.setFontHeightInPoints((short)16);
        font.setBold(true);
        headerStyle.setFont(font);

        createHeaderCell(header, 0, COLUMN_CUSTOMER_ID, headerStyle);
        createHeaderCell(header, 1, COLUMN_CUSTOMER_NAME, headerStyle);
        createHeaderCell(header, 2, COLUMN_CUSTOMER_PURCHASES, headerStyle);
    }

    /**
     * Creates a single header cell with styling
     */
    private void createHeaderCell(Row header, int column, String value, CellStyle style) {
        var cell = header.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    /**
     * Populates the Excel sheet with customer data
     */
    private void populateData(XSSFWorkbook workbook, Sheet sheet) {
        var style = workbook.createCellStyle();
        style.setWrapText(true);

        var customers = repository.findAll();
        var rowPos = 1;

        for (CustomerEntity customer : customers) {
            var row = sheet.createRow(rowPos++);
            createDataCell(row, 0, customer.getId().toString(), style);
            createDataCell(row, 1, customer.getName(), style);
            createDataCell(row, 2, String.valueOf(getTotalPurchase(customer)), style);
        }
    }

    /**
     * Creates a single data cell with styling
     */
    private void createDataCell(Row row, int column, String value, CellStyle style) {
        var cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    /**
     * Converts byte array to MultipartFile for upload
     */
    private MultipartFile convertToMultipartFile(byte[] data, String filename) {
        return new MultipartFile() {
            @Override
            public String getName() {
                return filename;
            }

            @Override
            public String getOriginalFilename() {
                return filename;
            }

            @Override
            public String getContentType() {
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            }

            @Override
            public boolean isEmpty() {
                return data.length == 0;
            }

            @Override
            public long getSize() {
                return data.length;
            }

            @Override
            public byte[] getBytes() {
                return data;
            }

            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(data);
            }

            @Override
            public void transferTo(File dest) throws IOException {
                Files.write(dest.toPath(), data);
            }
        };
    }

    private static int getTotalPurchase(CustomerEntity customer) {
        return customer.getTotalFlights() + customer.getTotalLodgings() + customer.getTotalTours();
    }

    // Constants
    private static final String SHEET_NAME = "Customer total Sales";
    private static final String FONT_TYPE = "Arial";
    private static final String COLUMN_CUSTOMER_ID = "id";
    private static final String COLUMN_CUSTOMER_NAME = "name";
    private static final String COLUMN_CUSTOMER_PURCHASES = "purchases";
}
