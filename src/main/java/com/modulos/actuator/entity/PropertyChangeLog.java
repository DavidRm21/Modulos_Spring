package com.modulos.actuator.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "property_audit_log")
public class PropertyChangeLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String propertyName;
    private String oldValue;
    private String newValue;
    private String username;
    private LocalDateTime changeDate;
    private String ipAddress;

}