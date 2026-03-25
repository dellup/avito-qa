package ru.avito.qa.api.endpoints;

import ru.avito.qa.api.client.ApiClient;
import ru.avito.qa.api.client.ApiResponse;

import java.util.Map;

public final class ItemByIdEndpoint extends BaseEndpoint {

    public ItemByIdEndpoint(ApiClient apiClient) {
        super(apiClient);
    }

    public ApiResponse getById(String id, Map<String, String> headers) {
        return apiClient.get("/api/1/item/" + encodePathSegment(id), headers);
    }
}
