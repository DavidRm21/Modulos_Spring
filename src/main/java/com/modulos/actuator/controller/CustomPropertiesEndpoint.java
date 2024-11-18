package com.modulos.actuator.controller;


import com.modulos.actuator.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Component
@Endpoint(id = "custom-properties")
public class CustomPropertiesEndpoint {

    @Autowired
    private PropertyService propertyService;

    @ReadOperation
    public Map<String, Object> getProperties() {
        return propertyService.getAllProperties();
    }

    @WriteOperation
    public Map<String, String> updateProperty(String name, String value) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Obtener IP del cliente
        String ipAddress = Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest)
                .map(request -> (String) request.getAttribute("clientIpAddress"))
                .orElse("unknown");

        propertyService.updateProperty(name, value, username, ipAddress);
        return Collections.singletonMap(name, value);
    }
}