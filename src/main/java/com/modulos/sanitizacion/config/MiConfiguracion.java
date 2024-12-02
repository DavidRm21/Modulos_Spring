package com.modulos.sanitizacion.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MiConfiguracion {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Map<String, List<Parametro>> mapa = new ConcurrentHashMap<>();

    public static void cargarConfiguracion(String base64Config) throws JsonProcessingException {
        // Decodificar el Base64
        String jsonConfig = new String(Base64.getDecoder().decode(base64Config));

        // Parsear el JSON
        JsonNode jsonNode = mapper.readTree(jsonConfig);

        // Iterar sobre todos los procedimientos en el JSON
        Iterator<Map.Entry<String, JsonNode>> procedimientos = jsonNode.fields();
        while (procedimientos.hasNext()) {
            Map.Entry<String, JsonNode> entry = procedimientos.next();
            String procedimientoNombre = entry.getKey();
            JsonNode procedureNode = entry.getValue();

            List<Parametro> parameters = new ArrayList<>();
            if (procedureNode.isArray()) {
                for (JsonNode paramNode : procedureNode) {
                    Parametro param = new Parametro();
                    param.setMode(paramNode.path("mode").asText());
                    param.setParamName(paramNode.path("paramName").asText());
                    param.setName(paramNode.path("name").asText());
                    param.setType(paramNode.path("type").asText());
                    parameters.add(param);
                }
            }
            // Agregar al mapa
            mapa.put(procedimientoNombre, parameters);
        }
    }

//    public static void main(String[] args)  {
//
//
//        String base = new String(Base64.getDecoder().decode("ewogICJQUk9fQ09OU1VMVEFSX0VTVEFETyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiY29kaWdvRW50aWRhZCIsCiAgICAgICJwYXJhbU5hbWUiOiAiUEFSX0NPRElHT19FTlRJREFEX0lOIiwKICAgICAgInR5cGUiOiAiTG9uZyIsCiAgICAgICJtb2RlIjogIklOIgogICAgfSwKICAgIHsKICAgICAgIm5hbWUiOiAidGlwb0RvY3VtZW50byIsCiAgICAgICJwYXJhbU5hbWUiOiAiUEFSX1RJUE9fRE9DVU1FTlRPX0lOIiwKICAgICAgInR5cGUiOiAiSW50ZWdlciIsCiAgICAgICJtb2RlIjogIklOIgogICAgfSwKICAgIHsKICAgICAgIm5hbWUiOiAiY29kaWdvUmVzcHVlc3RhIiwKICAgICAgInBhcmFtTmFtZSI6ICJQQVJfUkVTVUxUQURPX09VVCIsCiAgICAgICJ0eXBlIjogIkludGVnZXIiLAogICAgICAibW9kZSI6ICJPVVQiCiAgICB9CiAgXQp9"));
//
//        List<Parametro> parameters = new ArrayList<>();
//        JsonNode jsonNode = null;
//        try {
//            jsonNode = mapper.readTree(base);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//        JsonNode procedureNode = jsonNode.path("PRO_CONSULTAR_ESTADO");
//        if (procedureNode.isArray()) {
//            for (JsonNode paramNode : procedureNode) {
//                Parametro param = new Parametro();
//                param.setMode(paramNode.path("mode").asText());
//                param.setParamName(paramNode.path("paramName").asText());
//                param.setName(paramNode.path("name").asText());
//                param.setType(paramNode.path("type").asText());
//                parameters.add(param);
//            }
//        }
//        mapa.put("PRO_CONSULTAR_ESTADO", parameters);
//        System.out.println(mapa);
//    }


    public static void main(String[] args)  {
        // Ejemplo de uso con múltiples configuraciones Base64
        String[] configuraciones = {
                "ewogICJQUk9fQ09OU1VMVEFSX0VTVEFETyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiY29kaWdvRW50aWRhZCIsCiAgICAgICJwYXJhbU5hbWUiOiAiUEFSX0NPRElHT19FTlRJREFEX0lOIiwKICAgICAgInR5cGUiOiAiTG9uZyIsCiAgICAgICJtb2RlIjogIklOIgogICAgfSwKICAgIHsKICAgICAgIm5hbWUiOiAidGlwb0RvY3VtZW50byIsCiAgICAgICJwYXJhbU5hbWUiOiAiUEFSX1RJUE9fRE9DVU1FTlRPX0lOIiwKICAgICAgInR5cGUiOiAiSW50ZWdlciIsCiAgICAgICJtb2RlIjogIklOIgogICAgfSwKICAgIHsKICAgICAgIm5hbWUiOiAiY29kaWdvUmVzcHVlc3RhIiwKICAgICAgInBhcmFtTmFtZSI6ICJQQVJfUkVTVUxUQURPX09VVCIsCiAgICAgICJ0eXBlIjogIkludGVnZXIiLAogICAgICAibW9kZSI6ICJPVVQiCiAgICB9CiAgXSwKICAiUFJPX0NPTlNVTFRBX0VTVEFETyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiZW50aWRhZCIsCiAgICAgICJwYXJhbU5hbWUiOiAiUEFSX0NPRElHT19FTlRJREFEX0lOIiwKICAgICAgInR5cGUiOiAiTG9uZyIsCiAgICAgICJtb2RlIjogIklOIgogICAgfSwKICAgIHsKICAgICAgIm5hbWUiOiAidGlwb0RvY3VtZW50byIsCiAgICAgICJwYXJhbU5hbWUiOiAiUEFSX1RJUE9fRE9DVU1FTlRPX0lOIiwKICAgICAgInR5cGUiOiAiSW50ZWdlciIsCiAgICAgICJtb2RlIjogIklOIgogICAgfSwKICAgIHsKICAgICAgIm5hbWUiOiAicmVzcHVlc3RhIiwKICAgICAgInBhcmFtTmFtZSI6ICJQQVJfUkVTVUxUQURPX09VVCIsCiAgICAgICJ0eXBlIjogIkludGVnZXIiLAogICAgICAibW9kZSI6ICJPVVQiCiAgICB9CiAgXSwKICAiUFJPX0VTVEFETyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiY29kaWdvIiwKICAgICAgInBhcmFtTmFtZSI6ICJQQVJfQ09ESUdPX0VOVElEQURfSU4iLAogICAgICAidHlwZSI6ICJMb25nIiwKICAgICAgIm1vZGUiOiAiSU4iCiAgICB9LAogICAgewogICAgICAibmFtZSI6ICJ0aXBvRG9jdW1lbnRvIiwKICAgICAgInBhcmFtTmFtZSI6ICJQQVJfVElQT19ET0NVTUVOVE9fSU4iLAogICAgICAidHlwZSI6ICJJbnRlZ2VyIiwKICAgICAgIm1vZGUiOiAiSU4iCiAgICB9LAogICAgewogICAgICAibmFtZSI6ICJjb2RpZ29SZXNwdWVzdGEiLAogICAgICAicGFyYW1OYW1lIjogIlBBUl9SRVNVTFRBRE9fT1VUIiwKICAgICAgInR5cGUiOiAiSW50ZWdlciIsCiAgICAgICJtb2RlIjogIk9VVCIKICAgIH0KICBdCn0=",
                // Puedes agregar más configuraciones Base64 aquí
        };

        // Cargar todas las configuraciones
        for (String config : configuraciones) {
            try {
                cargarConfiguracion(config);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        // Imprimir configuraciones cargadas
        System.out.println("Configuraciones cargadas:");
        mapa.forEach((procedimiento, parametros) -> {
            System.out.println(procedimiento + ": " + parametros);
        });
    }

    // Método para obtener los parámetros de un procedimiento específico
    public static List<Parametro> obtenerParametrosProcedimiento(String procedimiento) {
        return mapa.get(procedimiento);
    }

}

