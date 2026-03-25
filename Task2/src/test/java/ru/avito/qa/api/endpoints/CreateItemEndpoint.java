package ru.avito.qa.api.endpoints;

import com.fasterxml.jackson.databind.JsonNode;
import ru.avito.qa.api.client.ApiClient;
import ru.avito.qa.api.client.ApiResponse;

import java.util.Map;

public final class CreateItemEndpoint extends BaseEndpoint {

    private static final String PATH = "/api/1/item";

    public CreateItemEndpoint(ApiClient apiClient) {
        super(apiClient);
    }

    public ApiResponse create(JsonNode payload, Map<String, String> headers) {
        return apiClient.post(PATH, payload, headers);
    }

    public ApiResponse createRaw(String body, Map<String, String> headers) {
        return apiClient.postRaw(PATH, body, headers);
    }
}
