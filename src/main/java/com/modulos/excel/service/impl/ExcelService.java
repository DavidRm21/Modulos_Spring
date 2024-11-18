package com.modulos.excel.service.impl;

import com.modulos.excel.entity.CustomerEntity;
import com.modulos.excel.repository.CustomerRepository;
import com.modulos.excel.service.ReportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

/**
 * Implementación de servicio para generar informes basados en Excel de datos de clientes.
 * Esta clase gestiona la creación y recuperación de archivos Excel que contienen información de compra de clientes utilizando la biblioteca POI de Apache.
 * información de compra utilizando la librería Apache POI.
 *
 * @implements Interfaz que define las operaciones de generación de informes.
 * @see CustomerRepository Para el acceso a los datos
 * @see XSSFWorkbook Para la manipulación de ficheros Excel
 */
@Service
@AllArgsConstructor
@Slf4j
public class ExcelService implements ReportService {

    /** Repositorio para acceder a los datos de los clientes */
    private CustomerRepository repository;

    /**
     * Lee el archivo de informe Excel generado y lo devuelve como una matriz de bytes.
     * El archivo se genera con el mes actual en su nombre si no existe.
     *
     * @return byte[] El contenido del fichero Excel como una matriz de bytes
     * @throws RuntimeException Si hay un error al leer el fichero
     */
    @Override
    public byte[] readFile() {
        try {
            this.createReport();
            var path = Paths.get(REPORT_PATH, String.format(FILE_NAME, LocalDate.now().getMonth())).toAbsolutePath();
            return Files.readAllBytes(path);
        }catch (IOException e){
            throw new RuntimeException();
        }
    }


    /**
     * Crea un informe Excel con los datos del cliente.
     * Este método maneja el proceso completo de:
     * 1. Creación de un nuevo libro de trabajo.
     * 2. 2. Estilizar la fila de encabezado
     * 3. Rellenar los datos del cliente
     * 4. Guardar el fichero en el sistema de ficheros
     *
     * @throws IllegalStateException Si no se puede crear el fichero Excel
     */
    private void createReport(){
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet(SHEET_NAME);

        // Establecer anchos de columna para una mejor legibilidad
        sheet.setColumnWidth(0, 5000);
        sheet.setColumnWidth(1, 7000);
        sheet.setColumnWidth(2, 3000);

        // Crear y estilizar la fila de cabecera
        var header = sheet.createRow(0);
        var headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Configurar las fuentes de la cabecera
        var font = workbook.createFont();
        font.setFontName(FONT_TYPE);
        font.setFontHeightInPoints((short)16);
        font.setBold(true);
        headerStyle.setFont(font);

        // Crear celdas de cabecera con estilo
        var headerCell = header.createCell(0);
        headerCell.setCellValue(COLUMN_CUSTOMER_ID);
        header.setRowStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue(COLUMN_CUSTOMER_NAME);
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue(COLUMN_CUSTOMER_PURCHASES);
        headerCell.setCellStyle(headerStyle);

        // Configurar el estilo de celda para las filas de datos
        var style = workbook.createCellStyle();
        style.setWrapText(true);

        // Rellenar los datos del cliente
        var customers = this.repository.findAll();
        var rowPos = 1;

        for (CustomerEntity customer: customers){
            var row = sheet.createRow(rowPos);
            var cell = row.createCell(1);
            cell.setCellValue(customer.getId());
            cell.setCellStyle(style);

            cell = row.createCell(2);
            cell.setCellValue(customer.getName());
            cell.setCellStyle(style);

            cell = row.createCell(3);
            cell.setCellValue(getTotalPurchase(customer));
            cell.setCellStyle(style);

            rowPos++;
        }

        // Guardar el libro en un archivo
        var report = new File(String.format(REPORT_PATH_WITH_NAME, LocalDate.now().getMonth()));
        var path = report.getAbsolutePath();
        var fileLocation = path + FILE_TYPE;


        try(var outputStream = new FileOutputStream(fileLocation)){
            workbook.write(outputStream);
            workbook.close();
        }catch (IOException e){
            log.error("No pudo crearse el excel: ", e);
            throw new IllegalStateException();
        }/*finally {
            Files.deleteIfExists(Path.of(fileLocation));
        }*/
    }

    /**
     * Calcula el total de compras de un cliente.
     * Combina el total de vuelos, alojamientos y tours.
     *
     * @param customer La entidad cliente para la que calcular los totales.
     * @return int El número total de compras
     */
    private static int getTotalPurchase(CustomerEntity customer){
        return customer.getTotalFlights() + customer.getTotalLodgings() + customer.getTotalTours();
    }

    /** Nombre de la hoja del libro de Excel */
    private static final String SHEET_NAME = "Customer total Sales";
    /** Tipo de fuente utilizado en el archivo Excel */
    private static final String FONT_TYPE = "Arial";
    /** Cabecera de columna para el ID del cliente */
    private static final String COLUMN_CUSTOMER_ID = "id";
    /** Column header for customer name */
    private static final String COLUMN_CUSTOMER_NAME = "name";
    /** Cabecera de columna para compras de clientes */
    private static final String COLUMN_CUSTOMER_PURCHASES = "purchases";
    /** Plantilla para la ruta del archivo de informe con el nombre */
    private static final String REPORT_PATH_WITH_NAME = "reports/sales-%s";
    /** Ruta base para el almacenamiento de informes */
    private static final String REPORT_PATH = "reports";
    /** Extensión de los ficheros Excel */
    private static final String FILE_TYPE = ".xlsx";
    /** Plantilla para el nombre del archivo de informe */
    private static final String FILE_NAME = "Sales-%s.xlsx";
}
