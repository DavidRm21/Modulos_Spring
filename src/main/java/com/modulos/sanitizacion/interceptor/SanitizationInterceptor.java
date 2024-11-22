package com.modulos.sanitizacion.interceptor;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SanitizationInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(SanitizationInterceptor.class);

    @Autowired
    private ObjectMapper objectMapper;
    private final Map<String, JsonSchema> schemaCache = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        log.warn("Sanitozado interceptor");
        if (!isJsonRequest(request)) {
            log.warn("return interceptor");
            return true;
        }
        log.warn("continua procesp");

        // Usamos BufferedReader para streaming y mejor manejo de memoria
        try (InputStream inputStream = request.getInputStream()) {
            // Leemos el body una sola vez
            JsonNode jsonNode = objectMapper.readTree(inputStream);

            // Validamos con el schema correspondiente
            String endpoint = request.getRequestURI();
            String method = request.getMethod();
            log.warn("endpoint: {}, method: {}", endpoint, method);

            // Aquí va tu lógica de validación con schemas
            log.warn("Validacion del json con el schema");
            //validateWithSchema(jsonNode, endpoint, method);

            // Sanitizamos y guardamos
            JsonNode sanitizedNode = sanitizeJsonNode(jsonNode);
            request.setAttribute("sanitizedBody", new WeakReference<>(sanitizedNode));
            log.warn("Atributp: {}", request.getAttribute("sanitizedBody").toString());

        } catch (Exception e) {
            log.error("Error processing request body", e);
            return false;
        }

        return true;

    }

    private boolean isJsonRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.contains("application/json");
    }

    private JsonNode parseJsonEfficiently(BufferedReader reader) throws IOException {
        // Usar JsonParser para streaming
        try (JsonParser parser = objectMapper.getFactory().createParser(reader)) {
            return objectMapper.readTree(parser);
        }
    }

    private JsonNode getSchemaForEndpoint(String endpoint) {
        //return schemaCache.computeIfAbsent(endpoint, this::loadSchema);
        log.warn("Obtiene el json schema");
        return objectMapper.createObjectNode();
    }

    private JsonNode sanitizeJsonNode(JsonNode node) {
        if (node.isObject()) {
            ObjectNode sanitized = objectMapper.createObjectNode();
            node.fields().forEachRemaining(entry -> {
                String sanitizedKey = sanitizeString(entry.getKey());
                JsonNode sanitizedValue = sanitizeJsonNode(entry.getValue());
                sanitized.set(sanitizedKey, sanitizedValue);
            });
            return sanitized;
        } else if (node.isArray()) {
            ArrayNode sanitized = objectMapper.createArrayNode();
            node.elements().forEachRemaining(element ->
                    sanitized.add(sanitizeJsonNode(element)));
            return sanitized;
        } else if (node.isTextual()) {
            return new TextNode(sanitizeString(node.asText()));
        }
        return node;
    }

    private String sanitizeString(String input) {
        // Tu lógica de sanitización aquí
        return input.replaceAll("<>", "");  // Ejemplo básico
    }
}