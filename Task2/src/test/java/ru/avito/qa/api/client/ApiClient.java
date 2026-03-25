package ru.avito.qa.api.client;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import ru.avito.qa.api.support.JsonSupport;

public final class ApiClient {

  private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
  private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(20);
  private static final String DEFAULT_BASE_URL = "https://qa-internship.avito.com";
  private static final int MAX_ATTEMPTS = 3;

  private final HttpClient httpClient;
  private final String baseUrl;

  public ApiClient() {
    this(resolveBaseUrl());
  }

  public ApiClient(String baseUrl) {
    this.baseUrl = trimTrailingSlash(Objects.requireNonNull(baseUrl, "baseUrl обязателен"));
    this.httpClient = HttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT).build();
  }

  public ApiResponse get(String path, Map<String, String> headers) {
    HttpRequest request = requestBuilder(path, headers).GET().build();
    return sendWithRetry(request, "GET", path);
  }

  public ApiResponse post(String path, JsonNode body, Map<String, String> headers) {
    String rawBody = body == null ? null : JsonSupport.toJson(body);
    return postRaw(path, rawBody, headers);
  }

  public ApiResponse postRaw(String path, String body, Map<String, String> headers) {
    HttpRequest request =
        requestBuilder(path, headers)
            .POST(
                body == null
                    ? HttpRequest.BodyPublishers.noBody()
                    : HttpRequest.BodyPublishers.ofString(body))
            .build();
    return sendWithRetry(request, "POST", path);
  }

  private HttpRequest.Builder requestBuilder(String path, Map<String, String> headers) {
    HttpRequest.Builder requestBuilder =
        HttpRequest.newBuilder(URI.create(baseUrl + path)).timeout(REQUEST_TIMEOUT);

    if (headers != null) {
      headers.forEach(requestBuilder::header);
    }
    return requestBuilder;
  }

  private ApiResponse sendWithRetry(HttpRequest request, String method, String path) {
    for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
      try {
        HttpResponse<String> response =
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return new ApiResponse(response.statusCode(), response.body(), response.headers());
      } catch (IOException exception) {
        if (attempt == MAX_ATTEMPTS) {
          throw new IllegalStateException(
              "HTTP-запрос завершился ошибкой: " + method + " " + path, exception);
        }
      } catch (InterruptedException exception) {
        Thread.currentThread().interrupt();
        throw new IllegalStateException(
            "HTTP-запрос был прерван: " + method + " " + path, exception);
      }
    }

    throw new IllegalStateException(
        "HTTP-запрос завершился ошибкой после повторов: " + method + " " + path);
  }

  private static String resolveBaseUrl() {
    String fromSystemProperty = System.getProperty("api.baseUrl");
    if (fromSystemProperty != null && !fromSystemProperty.isBlank()) {
      return fromSystemProperty;
    }
    String fromEnvironment = System.getenv("API_BASE_URL");
    if (fromEnvironment != null && !fromEnvironment.isBlank()) {
      return fromEnvironment;
    }
    return DEFAULT_BASE_URL;
  }

  private static String trimTrailingSlash(String value) {
    return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
  }
}
