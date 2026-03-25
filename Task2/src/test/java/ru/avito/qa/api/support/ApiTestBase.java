package ru.avito.qa.api.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assertions;
import ru.avito.qa.api.client.ApiClient;
import ru.avito.qa.api.client.ApiResponse;
import ru.avito.qa.api.endpoints.CreateItemEndpoint;
import ru.avito.qa.api.endpoints.ItemByIdEndpoint;
import ru.avito.qa.api.endpoints.SellerItemsEndpoint;
import ru.avito.qa.api.endpoints.StatisticEndpoint;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class ApiTestBase {

    private static final Pattern UUID_PATTERN = Pattern.compile(
            "([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})");

    protected final ApiClient apiClient = new ApiClient();
    protected final CreateItemEndpoint createItemEndpoint = new CreateItemEndpoint(apiClient);
    protected final ItemByIdEndpoint itemByIdEndpoint = new ItemByIdEndpoint(apiClient);
    protected final SellerItemsEndpoint sellerItemsEndpoint = new SellerItemsEndpoint(apiClient);
    protected final StatisticEndpoint statisticEndpoint = new StatisticEndpoint(apiClient);

    protected Map<String, String> jsonHeaders() {
        return Map.of("Content-Type", "application/json", "Accept", "application/json");
    }

    protected Map<String, String> acceptJsonHeader() {
        return Map.of("Accept", "application/json");
    }

    protected CreatedItem createValidItem() {
        return createValidItem(ItemPayloadFactory.randomSellerId());
    }

    protected CreatedItem createValidItem(int sellerId) {
        ObjectNode payload = ItemPayloadFactory.validPayload(sellerId);
        ApiResponse response = createItemEndpoint.create(payload, jsonHeaders());
        assertEquals(200, response.statusCode(),
                () -> "Не выполнено предусловие: создание валидного объявления вернуло " + response.statusCode() + ", тело: " + response.body());
        ObjectNode created = firstObject(response.json(), "создание валидного объявления");
        String id = extractCreatedItemId(created, "создание валидного объявления");
        int resolvedSellerId = created.has("sellerId") && created.get("sellerId").isNumber()
                ? created.get("sellerId").asInt()
                : payload.get("sellerID").asInt();
        return new CreatedItem(id, resolvedSellerId, payload);
    }

    protected ObjectNode firstObject(JsonNode responseJson, String context) {
        if (responseJson.isObject()) {
            return (ObjectNode) responseJson;
        }
        if (responseJson.isArray()) {
            assertTrue(responseJson.size() > 0, () -> context + ": массив ответа пустой");
            JsonNode first = responseJson.get(0);
            assertTrue(first.isObject(), () -> context + ": первый элемент массива должен быть объектом");
            return (ObjectNode) first;
        }
        Assertions.fail(context + ": ответ должен быть JSON-объектом или массивом");
        return null;
    }

    protected ArrayNode asArray(JsonNode responseJson, String context) {
        assertTrue(responseJson.isArray(), () -> context + ": ожидался JSON-массив");
        return (ArrayNode) responseJson;
    }

    protected String requiredText(ObjectNode node, String fieldName, String context) {
        JsonNode fieldValue = node.get(fieldName);
        assertTrue(fieldValue != null && fieldValue.isTextual(),
                () -> context + ": поле '" + fieldName + "' должно быть строкой");
        assertFalse(fieldValue.asText().isBlank(),
                () -> context + ": поле '" + fieldName + "' не должно быть пустым");
        return fieldValue.asText();
    }

    protected int requiredInt(ObjectNode node, String fieldName, String context) {
        JsonNode fieldValue = node.get(fieldName);
        assertTrue(fieldValue != null && fieldValue.isNumber(),
                () -> context + ": поле '" + fieldName + "' должно быть числом");
        return fieldValue.asInt();
    }

    protected void assertItemContract(ObjectNode item, String caseId) {
        requiredText(item, "id", caseId);
        requiredInt(item, "sellerId", caseId);
        requiredText(item, "name", caseId);
        requiredInt(item, "price", caseId);

        JsonNode statistics = item.get("statistics");
        assertTrue(statistics != null && statistics.isObject(),
                () -> "[" + caseId + "] Поле 'statistics' должно быть объектом");

        ObjectNode statisticsObject = (ObjectNode) statistics;
        requiredInt(statisticsObject, "likes", caseId);
        requiredInt(statisticsObject, "viewCount", caseId);
        requiredInt(statisticsObject, "contacts", caseId);
        requiredText(item, "createdAt", caseId);
    }

    protected void assertStatisticContract(ObjectNode statistics, String caseId) {
        requiredInt(statistics, "likes", caseId);
        requiredInt(statistics, "viewCount", caseId);
        requiredInt(statistics, "contacts", caseId);
    }

    protected Set<String> extractItemIds(ArrayNode items) {
        Set<String> ids = new LinkedHashSet<>();
        for (JsonNode item : items) {
            if (item.isObject() && item.hasNonNull("id")) {
                ids.add(item.get("id").asText());
            }
        }
        return ids;
    }

    private String extractCreatedItemId(ObjectNode createResponse, String context) {
        JsonNode directId = createResponse.get("id");
        if (directId != null && directId.isTextual() && !directId.asText().isBlank()) {
            return directId.asText();
        }

        JsonNode statusField = createResponse.get("status");
        if (statusField != null && statusField.isTextual()) {
            Matcher matcher = UUID_PATTERN.matcher(statusField.asText());
            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        Assertions.fail(context + ": в ответе на создание нет поля id и не удалось извлечь id из поля status");
        return null;
    }
}
