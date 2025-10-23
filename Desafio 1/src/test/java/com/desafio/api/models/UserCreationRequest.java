// src/test/java/com/desafio/api/models/UserCreationRequest.java
package com.desafio.api.models;

// Não precisa de bibliotecas de serialização complexas para este exemplo.

public class UserCreationRequest {
    private String userName;
    private String password;

    // Construtor
    public UserCreationRequest(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    // Getters e Setters (Obrigatórios para o Rest Assured mapear o JSON)
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
