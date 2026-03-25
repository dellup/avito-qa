package ru.avito.qa.api.endpoints;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import ru.avito.qa.api.client.ApiClient;

public abstract class BaseEndpoint {

  protected final ApiClient apiClient;

  protected BaseEndpoint(ApiClient apiClient) {
    this.apiClient = Objects.requireNonNull(apiClient, "apiClient обязателен");
  }

  protected static String encodePathSegment(String rawSegment) {
    return URLEncoder.encode(rawSegment, StandardCharsets.UTF_8).replace("+", "%20");
  }
}
