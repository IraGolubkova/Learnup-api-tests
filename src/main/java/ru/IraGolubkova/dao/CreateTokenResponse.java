package ru.IraGolubkova.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public class CreateTokenResponse {

    @Getter
    @JsonProperty("token")
    private final String token;

    public CreateTokenResponse(String token) {
        this.token = token;
    }
}

