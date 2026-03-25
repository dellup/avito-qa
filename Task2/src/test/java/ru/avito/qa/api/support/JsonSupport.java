package ru.avito.qa.api.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonSupport {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonSupport() {
    }

    public static JsonNode readJson(String json) {
        try {
            return OBJECT_MAPPER.readTree(json);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Тело ответа не является валидным JSON: " + json, exception);
        }
    }

    public static String toJson(JsonNode jsonNode) {
        try {
            return OBJECT_MAPPER.writeValueAsString(jsonNode);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Не удалось сериализовать тело запроса в JSON", exception);
        }
    }
}
