package ru.avito.qa.api.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import ru.avito.qa.api.client.ApiResponse;
import ru.avito.qa.api.support.ApiTestBase;
import ru.avito.qa.api.support.CreatedItem;
import ru.avito.qa.api.support.ItemPayloadFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class E2EApiFlowTest extends ApiTestBase {

    @Test
    void tc77_createThenGetByIdSellerAndStatistic() {
        CreatedItem createdItem = createValidItem();

        ApiResponse getByIdResponse = itemByIdEndpoint.getById(createdItem.id(), acceptJsonHeader());
        assertEquals(200, getByIdResponse.statusCode(), "[E2E-01] GET /item/{id} должен вернуть 200");
        ObjectNode itemById = firstObject(getByIdResponse.json(), "E2E-01 GET item");
        assertEquals(createdItem.id(), requiredText(itemById, "id", "E2E-01"));
        assertEquals(createdItem.sellerId(), requiredInt(itemById, "sellerId", "E2E-01"));

        ApiResponse getBySellerResponse = sellerItemsEndpoint.getBySellerId(String.valueOf(createdItem.sellerId()), acceptJsonHeader());
        assertEquals(200, getBySellerResponse.statusCode(), "[E2E-01] GET /{sellerID}/item должен вернуть 200");
        ArrayNode sellerItems = asArray(getBySellerResponse.json(), "E2E-01 GET seller items");
        assertTrue(extractItemIds(sellerItems).contains(createdItem.id()),
                "[E2E-01] Список объявлений продавца должен содержать созданный id");

        ApiResponse statisticResponse = statisticEndpoint.getByItemId(createdItem.id(), acceptJsonHeader());
        assertEquals(200, statisticResponse.statusCode(), "[E2E-01] GET /statistic/{id} должен вернуть 200");
        ObjectNode statistics = firstObject(statisticResponse.json(), "E2E-01 GET statistic");
        assertStatisticContract(statistics, "E2E-01");

        JsonNode expectedStatistics = createdItem.requestPayload().get("statistics");
        assertEquals(expectedStatistics.get("likes").asInt(), statistics.get("likes").asInt(),
                "[E2E-01] likes должен совпадать с отправленным значением");
        assertEquals(expectedStatistics.get("viewCount").asInt(), statistics.get("viewCount").asInt(),
                "[E2E-01] viewCount должен совпадать с отправленным значением");
        assertEquals(expectedStatistics.get("contacts").asInt(), statistics.get("contacts").asInt(),
                "[E2E-01] contacts должен совпадать с отправленным значением");
    }
}

