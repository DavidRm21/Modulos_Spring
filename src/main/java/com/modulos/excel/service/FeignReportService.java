package com.modulos.excel.service;

public interface FeignReportService {

    String generateAndUploadReport();
    byte[] generateAndUploadReportBytes();
//    FileUploadResponse generateAndUploadReportBytes();

}
