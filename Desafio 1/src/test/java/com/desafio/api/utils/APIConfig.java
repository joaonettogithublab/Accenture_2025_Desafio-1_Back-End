// src/test/java/com/desafio/api/utils/APIConfig.java
package com.desafio.api.utils;

import io.restassured.RestAssured;

public class APIConfig {
    public static void setup() {
        // Configuração da URI base conforme anexo
        RestAssured.baseURI = "https://demoqa.com";
    }
}