package com.modulos.excel.entity;

import com.modulos.excel.interfaces.ExcelColumn;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "CUSTOMER_ENTITY")
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ExcelColumn(header = "ID", order = 1, width = 3000)
    private Long id;

    @ExcelColumn(header = "Nombre Cliente", order = 2, width = 7000)
    private String name;

    @ExcelColumn(header = "Email", order = 3)
    private Integer totalLodgings;

    @ExcelColumn(header = "Fecha Registro", order = 4, width = 4000)
    private Integer totalFlights;

    @ExcelColumn(header = "Total Compras", order = 5, width = 4000)
    private Integer totalTours;
}