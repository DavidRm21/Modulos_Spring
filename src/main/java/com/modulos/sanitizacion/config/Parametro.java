package com.modulos.sanitizacion.config;

import jakarta.persistence.ParameterMode;

public class Parametro {
        private String name;
        private String paramName;
        private Class<?> type;
        private ParameterMode mode;

    public Parametro() {
    }

    public Parametro(String name, String paramName, Class<?> type, ParameterMode mode) {
        this.name = name;
        this.paramName = paramName;
        this.type = type;
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "Parametro{" +
                "name='" + name + '\'' +
                ", paramName='" + paramName + '\'' +
                ", type=" + type +
                ", mode=" + mode +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(String type) {
        this.type = switch (type.toLowerCase()){
            case "long"-> Long.class;
            case "integer" -> Integer.class;
            case "string" -> String.class;
            default -> throw new IllegalStateException("Unexpected value: " + type.toLowerCase());
        };
    }

    public ParameterMode getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = switch (mode.toLowerCase()){
            case "in" -> ParameterMode.IN;
            case "out" -> ParameterMode.OUT;
            case "inout" -> ParameterMode.INOUT;
            case "refcursor" -> ParameterMode.REF_CURSOR;
            default -> throw new IllegalStateException("Unexpected value: " + mode.toLowerCase());
        };
    }
}
