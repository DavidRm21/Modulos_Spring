package com.modulos.actuator.repository;

import com.modulos.actuator.entity.PropertyChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyChangeLogRepository extends JpaRepository<PropertyChangeLog, Long> {
    List<PropertyChangeLog> findByPropertyNameOrderByChangeDateDesc(String propertyName);
}

