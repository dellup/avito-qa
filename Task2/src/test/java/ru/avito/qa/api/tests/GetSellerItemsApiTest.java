package ru.avito.qa.api.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.avito.qa.api.cases.HeaderScenario;
import ru.avito.qa.api.cases.PathScenario;
import ru.avito.qa.api.client.ApiResponse;
import ru.avito.qa.api.support.ApiTestBase;
import ru.avito.qa.api.support.CreatedItem;
import ru.avito.qa.api.support.TestAssertions;

public class GetSellerItemsApiTest extends ApiTestBase {

  @Test
  void tc48_successGetItemsForExistingSeller() {
    CreatedItem createdItem = createValidItem();

    ApiResponse response =
        sellerItemsEndpoint.getBySellerId(
            String.valueOf(createdItem.sellerId()), acceptJsonHeader());

    assertEquals(200, response.statusCode(), "[TC-48] Ожидался статус 200");
    ArrayNode items = asArray(response.json(), "TC-48 ответ");
    assertTrue(
        items.size() > 0, "[TC-48] Ответ со списком объявлений продавца не должен быть пустым");
    assertTrue(
        extractItemIds(items).contains(createdItem.id()),
        "[TC-48] Ответ должен содержать id созданного объявления");
  }

  @Test
  void tc49_responseStructureForGetSellerItems() {
    CreatedItem createdItem = createValidItem();

    ApiResponse response =
        sellerItemsEndpoint.getBySellerId(
            String.valueOf(createdItem.sellerId()), acceptJsonHeader());

    assertEquals(200, response.statusCode(), "[TC-49] Ожидался статус 200");
    ArrayNode items = asArray(response.json(), "TC-49 ответ");
    assertTrue(
        items.size() > 0, "[TC-49] Ответ со списком объявлений продавца не должен быть пустым");
    assertItemContract((ObjectNode) items.get(0), "TC-49");
  }

  @Test
  void tc50_onlyRequestedSellerItemsShouldBeReturned() {
    CreatedItem createdItem = createValidItem();

    ApiResponse response =
        sellerItemsEndpoint.getBySellerId(
            String.valueOf(createdItem.sellerId()), acceptJsonHeader());

    assertEquals(200, response.statusCode(), "[TC-50] Ожидался статус 200");
    ArrayNode items = asArray(response.json(), "TC-50 ответ");
    assertTrue(
        items.size() > 0, "[TC-50] Ответ со списком объявлений продавца не должен быть пустым");
    for (JsonNode item : items) {
      assertEquals(
          createdItem.sellerId(),
          item.get("sellerId").asInt(),
          "[TC-50] В ответе найдено объявление другого продавца");
    }
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("ru.avito.qa.api.cases.PathScenarioProvider#invalidSellerIdCases")
  void tc51to54_getSellerItemsWithInvalidSellerId(PathScenario scenario) {
    ApiResponse response =
        sellerItemsEndpoint.getBySellerId(scenario.pathValue(), acceptJsonHeader());

    assertEquals(
        scenario.expectedStatus(),
        response.statusCode(),
        () -> "[" + scenario.caseId() + "] Неожиданный статус. Тело: " + response.body());
    TestAssertions.assertErrorPayloadPresent(response, scenario.caseId());
  }

  @Test
  void tc55_repeatGetSellerItemsShouldReturnStableList() {
    CreatedItem createdItem = createValidItem();

    ApiResponse firstResponse =
        sellerItemsEndpoint.getBySellerId(
            String.valueOf(createdItem.sellerId()), acceptJsonHeader());
    ApiResponse secondResponse =
        sellerItemsEndpoint.getBySellerId(
            String.valueOf(createdItem.sellerId()), acceptJsonHeader());

    assertEquals(200, firstResponse.statusCode(), "[TC-55] Первый GET должен вернуть 200");
    assertEquals(200, secondResponse.statusCode(), "[TC-55] Второй GET должен вернуть 200");

    Set<String> firstIds = extractItemIds(asArray(firstResponse.json(), "TC-55 первый ответ"));
    Set<String> secondIds = extractItemIds(asArray(secondResponse.json(), "TC-55 второй ответ"));

    assertEquals(
        firstIds,
        secondIds,
        "[TC-55] Повторные ответы по объявлениям продавца должны быть стабильными");
    assertTrue(
        firstIds.contains(createdItem.id()),
        "[TC-55] id созданного объявления должен присутствовать в обоих ответах");
  }

  @Test
  void tc65_getSellerItemsWithCorrectAcceptHeader() {
    CreatedItem createdItem = createValidItem();

    ApiResponse response =
        sellerItemsEndpoint.getBySellerId(
            String.valueOf(createdItem.sellerId()), acceptJsonHeader());

    assertEquals(
        200, response.statusCode(), "[TC-65] Ожидался статус 200 с Accept: application/json");
    ArrayNode items = asArray(response.json(), "TC-65 ответ");
    assertTrue(
        extractItemIds(items).contains(createdItem.id()),
        "[TC-65] Ответ должен содержать id созданного объявления");
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("ru.avito.qa.api.cases.HeaderScenarioProvider#getSellerItemsHeaderCases")
  void tc73to74_getSellerItemsWithMissingOrInvalidAccept(HeaderScenario scenario) {
    CreatedItem createdItem = createValidItem();

    ApiResponse response =
        sellerItemsEndpoint.getBySellerId(
            String.valueOf(createdItem.sellerId()), scenario.headers());

    TestAssertions.assertControlledStatus(response, scenario.caseId());
    if (response.statusCode() == 200) {
      ArrayNode items = asArray(response.json(), scenario.caseId() + " ответ");
      assertTrue(
          extractItemIds(items).contains(createdItem.id()),
          () ->
              "["
                  + scenario.caseId()
                  + "] id созданного объявления должен присутствовать в ответе");
    }
  }
}
