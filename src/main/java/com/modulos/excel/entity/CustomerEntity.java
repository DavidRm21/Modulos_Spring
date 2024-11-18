package com.modulos.excel.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "CUSTOMER_ENTITY")
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer totalLodgings;

    private Integer totalFlights;

    private Integer totalTours;
}