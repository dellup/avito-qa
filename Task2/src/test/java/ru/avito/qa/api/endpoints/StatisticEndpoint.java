package ru.avito.qa.api.endpoints;

import java.util.Map;
import ru.avito.qa.api.client.ApiClient;
import ru.avito.qa.api.client.ApiResponse;

public final class StatisticEndpoint extends BaseEndpoint {

  public StatisticEndpoint(ApiClient apiClient) {
    super(apiClient);
  }

  public ApiResponse getByItemId(String id, Map<String, String> headers) {
    return apiClient.get("/api/1/statistic/" + encodePathSegment(id), headers);
  }
}
