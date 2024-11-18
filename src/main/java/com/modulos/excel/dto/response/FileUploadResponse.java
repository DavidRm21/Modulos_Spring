package com.modulos.excel.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response object for file upload
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadResponse {
    private String fileUrl;
    private String fileId;
    private long size;
}
