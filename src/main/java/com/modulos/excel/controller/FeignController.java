package com.modulos.excel.controller;

import com.modulos.excel.service.FeignReportService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling report download requests
 */
@RestController
@RequestMapping(path = "api")
@AllArgsConstructor
public class FeignController {

    private final FeignReportService reportService;

    @GetMapping("/report")
    public ResponseEntity<String> generateReport() {
        String fileLocation = reportService.generateAndUploadReport();
        return ResponseEntity.ok(fileLocation);
    }

    @GetMapping("/reportbyte")
    public ResponseEntity<byte[]> generateReportBytes() {
        byte[] fileUploadResponse = reportService.generateAndUploadReportBytes();
        return ResponseEntity.ok(fileUploadResponse);
    }
}
