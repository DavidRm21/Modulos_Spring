package com.modulos.excel.com;

import com.modulos.excel.dto.response.FileUploadResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.multipart.MultipartFile;

//@FeignClient(name = "storage", url = "${storage.url}")
@FeignClient(name = "storage", url = "localhost")
public interface StorageServiceClient {

    /**
     * Uploads a file to the storage service
     * @param file MultipartFile to be uploaded
     * @return String URL or identifier of stored file
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadFile(@SpringQueryMap MultipartFile file);

    /**
     * Uploads file content as byte array
     *
     * @param filename Name of the file to be stored
     * @param contentType MIME type of the file
     * @param content Byte array containing file data
     * @return String URL or identifier of stored file
     */
    @PostMapping(value = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    FileUploadResponse uploadFile(
            @RequestHeader("X-File-Name") String filename,
            @RequestHeader("Content-Type") String contentType,
            @RequestBody byte[] content
    );
}
