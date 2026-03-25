package ru.avito.qa.api.support;

import com.fasterxml.jackson.databind.JsonNode;
import ru.avito.qa.api.client.ApiResponse;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class TestAssertions {

    private TestAssertions() {
    }

    public static void assertControlledStatus(ApiResponse response, String caseId) {
        int statusCode = response.statusCode();
        assertTrue(statusCode >= 200 && statusCode < 500,
                () -> "[" + caseId + "] Ожидался статус из 2xx или 4xx, получен " + statusCode);
    }

    public static void assertErrorPayloadPresent(ApiResponse response, String caseId) {
        String body = response.body();
        assertFalse(body == null || body.isBlank(),
                () -> "[" + caseId + "] Ожидалось непустое тело ответа с ошибкой");

        JsonNode jsonNode = response.json();
        assertTrue(jsonNode.isObject() || jsonNode.isArray(),
                () -> "[" + caseId + "] Тело ошибки должно быть JSON-объектом или массивом");

        if (jsonNode.isObject()) {
            assertTrue(jsonNode.has("result") || jsonNode.has("message") || jsonNode.has("status"),
                    () -> "[" + caseId + "] Тело ошибки должно содержать детали ошибки");
        }
    }
}
