package com.modulos.sanitizacion;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class FilterController {

    @PostMapping
    public ResponseEntity<String> post(@RequestBody JsonNode jsonNode){
        System.out.println(jsonNode);
        return ResponseEntity.ok(jsonNode.toPrettyString());
    }
}
