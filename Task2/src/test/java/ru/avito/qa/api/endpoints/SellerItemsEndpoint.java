package ru.avito.qa.api.endpoints;

import ru.avito.qa.api.client.ApiClient;
import ru.avito.qa.api.client.ApiResponse;

import java.util.Map;

public final class SellerItemsEndpoint extends BaseEndpoint {

    public SellerItemsEndpoint(ApiClient apiClient) {
        super(apiClient);
    }

    public ApiResponse getBySellerId(String sellerId, Map<String, String> headers) {
        return apiClient.get("/api/1/" + encodePathSegment(sellerId) + "/item", headers);
    }
}
