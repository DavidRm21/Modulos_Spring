package com.modulos.excel.controller;

import com.modulos.excel.service.ReportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Controlador REST para gestionar las solicitudes de descarga de informes.
 * Proporciona puntos finales para la descarga de informes Excel con cabeceras adecuadas
 * y tipos de contenido para forzar el comportamiento de descarga del navegador.
 *
 * @RestController Marca esta clase como un controlador REST
 * @RequestMapping Mapea a la ruta «api
 */
@RestController
@RequestMapping(path = "api")
@AllArgsConstructor
@Slf4j
public class ReportController {

    /** Servicio de generación y recuperación de informes */
    private final ReportService reportService;
    private final Environment env;

    /**
     * Gestiona las peticiones GET para la descarga de informes.
     * Configura las cabeceras de respuesta para forzar el comportamiento de descarga en los navegadores.
     * y devuelve el archivo del informe como un recurso descargable.
     *
     * @return ResponseEntity<Resource> El fichero de informe como recurso descargable.
     * @throws RuntimeException Si el informe no puede ser generado o leído
     */
    @GetMapping("file")
    private ResponseEntity<Resource> get(){
        var headers = new HttpHeaders();
        headers.setContentType(FORCE_DOWNLOAD);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, FORCE_DOWNLOAD_HEADER_VALUE);

        var fileInBytes = this.reportService.readFile();

        ByteArrayResource resource = new ByteArrayResource(fileInBytes);

        log.info("resource: {}", resource);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileInBytes.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    /** Tipo de medio para forzar el comportamiento de descarga del navegador */
    private static final MediaType FORCE_DOWNLOAD = new MediaType("application", "force_download");
    /** Valor de cabecera para especificar el nombre del archivo de descarga */
    private static final String FORCE_DOWNLOAD_HEADER_VALUE = "attachment; filename=report.xlsx";

    @GetMapping("bytes")
    private ResponseEntity<String> getBytes() throws IOException {

        var fileInBytes = this.reportService.readFile();
        String response = new String(fileInBytes, StandardCharsets.UTF_8);

        log.info("response: {}", response);
        return ResponseEntity.ok(response);
    }


    @GetMapping("props")
    public ResponseEntity<?> props(){
        String numero = env.getProperty("numero");
        String rosa = env.getProperty("rosa");
        String nombre = env.getProperty("nombre");

        return ResponseEntity.ok(nombre +" "+ rosa +" "+ numero);
    }
}
