package ru.avito.qa.api.client;

import com.fasterxml.jackson.databind.JsonNode;
import ru.avito.qa.api.support.JsonSupport;

import java.net.http.HttpHeaders;

public record ApiResponse(int statusCode, String body, HttpHeaders headers) {

    public JsonNode json() {
        if (body == null || body.isBlank()) {
            throw new IllegalStateException("Тело ответа пустое");
        }
        return JsonSupport.readJson(body);
    }
}
