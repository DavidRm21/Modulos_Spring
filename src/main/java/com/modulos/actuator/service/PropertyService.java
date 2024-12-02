package com.modulos.actuator.service;

import com.modulos.actuator.entity.PropertyChangeLog;
import com.modulos.actuator.repository.PropertyChangeLogRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PropertyService {

    @Autowired
    private ConfigurableEnvironment environment;

    @Autowired
    private PropertyChangeLogRepository changeLogRepository;

    @Autowired
    private PropertyValidator propertyValidator;

//    @Value("${property.restricted.list}")
//    private List<String> restrictedProperties = Arrays.asList("spring.datasource.password",
//            "spring.security.user.password");

    public Map<String, Object> getAllProperties() {
        Map<String, Object> props = new HashMap<>();
        environment.getPropertySources().forEach(propertySource -> {
            if (propertySource instanceof EnumerablePropertySource) {
                EnumerablePropertySource<?> enumerable = (EnumerablePropertySource<?>) propertySource;
                Arrays.stream(enumerable.getPropertyNames())
                        .filter(this::isNotRestricted)
                        .forEach(prop -> props.put(prop, environment.getProperty(prop)));
            }
        });
        return props;
    }

    public void updateProperty(String name, String value, String username, String ipAddress) {
        // Validación de seguridad
        if (isRestricted(name)) {
            throw new SecurityException("No está permitido modificar esta propiedad: " + name);
        }

        // Validación del valor
        propertyValidator.validate(name, value);

        // Obtener valor actual
        String oldValue = environment.getProperty(name);

        // Actualizar propiedad
        MutablePropertySources propertySources = environment.getPropertySources();
        MapPropertySource propertySource = (MapPropertySource) propertySources
                .get("dynamicProperties");

        if (propertySource == null) {
            Map<String, Object> map = new HashMap<>();
            propertySource = new MapPropertySource("dynamicProperties", map);
            propertySources.addFirst(propertySource);
        }

        ((Map<String, Object>) propertySource.getSource()).put(name, value);

        // Registrar el cambio
        PropertyChangeLog log = new PropertyChangeLog();
        log.setPropertyName(name);
        log.setOldValue(oldValue);
        log.setNewValue(value);
        log.setUsername(username);
        log.setIpAddress(ipAddress);
        log.setChangeDate(LocalDateTime.now());

        changeLogRepository.save(log);
    }

    private boolean isRestricted(String propertyName) {
        return false;
//        return restrictedProperties.stream()
//                .anyMatch(propertyName::startsWith);
    }

    private boolean isNotRestricted(String propertyName) {
        return !isRestricted(propertyName);
    }
}
