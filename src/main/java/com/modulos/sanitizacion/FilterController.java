package com.modulos.sanitizacion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.modulos.sanitizacion.config.MiConfiguracion;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
public class FilterController {

//    @Value("#{${numero}}")
//    private Map<String, String> numeros;


    private final Environment env;

    @PostMapping
    public ResponseEntity<String> post(@RequestBody JsonNode jsonNode){
//        System.out.println(jsonNode);
//        String valie = env.getProperty("numero");
//
//        MiConfiguracion numeroConfig = new MiConfiguracion();
//        Map<String, String> schemas = numeroConfig.getNumeros();
//        Map<String, String> schemas2 = numeroConfig.getNumeros2();
//        System.err.println(schemas);
//        System.err.println();
//        System.err.println(schemas2);
//
//        String schema = env.getProperty("spring.schemas");
//        ((ObjectNode)jsonNode).put("yml", valie);
//        ((ObjectNode)jsonNode).put("schema", schema);
        return ResponseEntity.ok(jsonNode.toPrettyString());
    }
}
