package ru.avito.qa.api.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.avito.qa.api.cases.HeaderScenario;
import ru.avito.qa.api.cases.PathScenario;
import ru.avito.qa.api.client.ApiResponse;
import ru.avito.qa.api.support.ApiTestBase;
import ru.avito.qa.api.support.CreatedItem;
import ru.avito.qa.api.support.TestAssertions;

public class GetItemByIdApiTest extends ApiTestBase {

  @Test
  void tc41_successGetItemByExistingId() {
    CreatedItem createdItem = createValidItem();

    ApiResponse response = itemByIdEndpoint.getById(createdItem.id(), acceptJsonHeader());

    assertEquals(200, response.statusCode(), "[TC-41] Ожидался статус 200");
    ObjectNode item = firstObject(response.json(), "TC-41 ответ");
    assertEquals(
        createdItem.id(),
        requiredText(item, "id", "TC-41"),
        "[TC-41] Ответ должен содержать запрошенный id объявления");
  }

  @Test
  void tc42_responseStructureForGetById() {
    CreatedItem createdItem = createValidItem();

    ApiResponse response = itemByIdEndpoint.getById(createdItem.id(), acceptJsonHeader());

    assertEquals(200, response.statusCode(), "[TC-42] Ожидался статус 200");
    assertItemContract(firstObject(response.json(), "TC-42 ответ"), "TC-42");
  }

  @Test
  void tc43_getNonExistingItemById() {
    String nonExistingId = "no-such-id-" + UUID.randomUUID();

    ApiResponse response = itemByIdEndpoint.getById(nonExistingId, acceptJsonHeader());

    assertEquals(404, response.statusCode(), "[TC-43] Ожидался статус 404 для несуществующего id");
    TestAssertions.assertErrorPayloadPresent(response, "TC-43");
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("ru.avito.qa.api.cases.PathScenarioProvider#invalidItemIdCases")
  void tc44to46_getByIdWithInvalidPath(PathScenario scenario) {
    ApiResponse response = itemByIdEndpoint.getById(scenario.pathValue(), acceptJsonHeader());

    assertEquals(
        scenario.expectedStatus(),
        response.statusCode(),
        () -> "[" + scenario.caseId() + "] Неожиданный статус. Тело: " + response.body());
    TestAssertions.assertErrorPayloadPresent(response, scenario.caseId());
  }

  @Test
  void tc47_repeatGetByIdShouldReturnStableData() {
    CreatedItem createdItem = createValidItem();

    ApiResponse firstResponse = itemByIdEndpoint.getById(createdItem.id(), acceptJsonHeader());
    ApiResponse secondResponse = itemByIdEndpoint.getById(createdItem.id(), acceptJsonHeader());

    assertEquals(200, firstResponse.statusCode(), "[TC-47] Первый GET должен вернуть 200");
    assertEquals(200, secondResponse.statusCode(), "[TC-47] Второй GET должен вернуть 200");

    ObjectNode firstItem = firstObject(firstResponse.json(), "TC-47 первый ответ");
    ObjectNode secondItem = firstObject(secondResponse.json(), "TC-47 второй ответ");

    assertEquals(
        firstItem, secondItem, "[TC-47] Два GET-ответа для одного id должны быть идентичными");
  }

  @Test
  void tc64_getByIdWithCorrectAcceptHeader() {
    CreatedItem createdItem = createValidItem();

    ApiResponse response = itemByIdEndpoint.getById(createdItem.id(), acceptJsonHeader());

    assertEquals(
        200, response.statusCode(), "[TC-64] Ожидался статус 200 с Accept: application/json");
    ObjectNode item = firstObject(response.json(), "TC-64 ответ");
    assertEquals(createdItem.id(), requiredText(item, "id", "TC-64"));
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("ru.avito.qa.api.cases.HeaderScenarioProvider#getItemHeaderCases")
  void tc71to72_getByIdWithMissingOrInvalidAccept(HeaderScenario scenario) {
    CreatedItem createdItem = createValidItem();

    ApiResponse response = itemByIdEndpoint.getById(createdItem.id(), scenario.headers());

    TestAssertions.assertControlledStatus(response, scenario.caseId());
    if (response.statusCode() == 200) {
      ObjectNode item = firstObject(response.json(), scenario.caseId() + " ответ");
      assertEquals(createdItem.id(), requiredText(item, "id", scenario.caseId()));
    }
  }
}
