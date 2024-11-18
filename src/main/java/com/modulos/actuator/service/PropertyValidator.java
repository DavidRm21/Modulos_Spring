package com.modulos.actuator.service;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class PropertyValidator {

    private final Map<String, PropertyValidationRule> validationRules;

    public PropertyValidator() {
        validationRules = new HashMap<>();
        // Reglas de validación predefinidas
        validationRules.put("server.port", value -> {
            try {
                int port = Integer.parseInt(value);
                return port >= 1024 && port <= 65535;
            } catch (NumberFormatException e) {
                return false;
            }
        });

        validationRules.put("logging.level.", value ->
                Arrays.asList("TRACE", "DEBUG", "INFO", "WARN", "ERROR")
                        .contains(value.toUpperCase()));
    }

    public void validate(String name, String value) {
        PropertyValidationRule rule = validationRules.entrySet().stream()
                .filter(entry -> name.startsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(val -> true);  // Regla por defecto: acepta cualquier valor

        if (!rule.isValid(value)) {
            throw new IllegalArgumentException(
                    "Valor inválido para la propiedad " + name + ": " + value);
        }
    }

    @FunctionalInterface
    interface PropertyValidationRule {
        boolean isValid(String value);
    }
}
