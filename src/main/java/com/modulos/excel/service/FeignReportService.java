package com.modulos.excel.service;

import java.util.List;

public interface FeignReportService {

    String generateAndUploadReport();
    byte[] generateAndUploadReportBytes();
//    FileUploadResponse generateAndUploadReportBytes();

    byte[] generateCustomerReport();

}
