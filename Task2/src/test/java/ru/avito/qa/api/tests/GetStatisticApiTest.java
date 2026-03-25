package ru.avito.qa.api.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
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

public class GetStatisticApiTest extends ApiTestBase {

  @Test
  void tc56_successGetStatisticByExistingItemId() {
    CreatedItem createdItem = createValidItem();

    ApiResponse response = statisticEndpoint.getByItemId(createdItem.id(), acceptJsonHeader());

    assertEquals(200, response.statusCode(), "[TC-56] Ожидался статус 200");
    assertStatisticContract(firstObject(response.json(), "TC-56 ответ"), "TC-56");
  }

  @Test
  void tc57_responseStructureForGetStatistic() {
    CreatedItem createdItem = createValidItem();

    ApiResponse response = statisticEndpoint.getByItemId(createdItem.id(), acceptJsonHeader());

    assertEquals(200, response.statusCode(), "[TC-57] Ожидался статус 200");
    assertStatisticContract(firstObject(response.json(), "TC-57 ответ"), "TC-57");
  }

  @Test
  void tc58_statisticShouldMatchValuesFromCreatedItem() {
    CreatedItem createdItem = createValidItem();

    ApiResponse response = statisticEndpoint.getByItemId(createdItem.id(), acceptJsonHeader());

    assertEquals(200, response.statusCode(), "[TC-58] Ожидался статус 200");
    ObjectNode statistics = firstObject(response.json(), "TC-58 ответ");

    JsonNode expectedStatistics = createdItem.requestPayload().get("statistics");
    assertEquals(
        expectedStatistics.get("likes").asInt(),
        statistics.get("likes").asInt(),
        "[TC-58] likes должен совпадать со значением из POST-запроса");
    assertEquals(
        expectedStatistics.get("viewCount").asInt(),
        statistics.get("viewCount").asInt(),
        "[TC-58] viewCount должен совпадать со значением из POST-запроса");
    assertEquals(
        expectedStatistics.get("contacts").asInt(),
        statistics.get("contacts").asInt(),
        "[TC-58] contacts должен совпадать со значением из POST-запроса");
  }

  @Test
  void tc59_getStatisticForNonExistingItemId() {
    String nonExistingId = "no-stat-id-" + UUID.randomUUID();

    ApiResponse response = statisticEndpoint.getByItemId(nonExistingId, acceptJsonHeader());

    assertEquals(404, response.statusCode(), "[TC-59] Ожидался статус 404 для несуществующего id");
    TestAssertions.assertErrorPayloadPresent(response, "TC-59");
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("ru.avito.qa.api.cases.PathScenarioProvider#invalidStatisticIdCases")
  void tc60to61_getStatisticWithInvalidId(PathScenario scenario) {
    ApiResponse response = statisticEndpoint.getByItemId(scenario.pathValue(), acceptJsonHeader());

    assertEquals(
        scenario.expectedStatus(),
        response.statusCode(),
        () -> "[" + scenario.caseId() + "] Неожиданный статус. Тело: " + response.body());
    TestAssertions.assertErrorPayloadPresent(response, scenario.caseId());
  }

  @Test
  void tc62_repeatGetStatisticShouldReturnStableData() {
    CreatedItem createdItem = createValidItem();

    ApiResponse firstResponse = statisticEndpoint.getByItemId(createdItem.id(), acceptJsonHeader());
    ApiResponse secondResponse =
        statisticEndpoint.getByItemId(createdItem.id(), acceptJsonHeader());

    assertEquals(200, firstResponse.statusCode(), "[TC-62] Первый GET должен вернуть 200");
    assertEquals(200, secondResponse.statusCode(), "[TC-62] Второй GET должен вернуть 200");

    ObjectNode firstStatistics = firstObject(firstResponse.json(), "TC-62 первый ответ");
    ObjectNode secondStatistics = firstObject(secondResponse.json(), "TC-62 второй ответ");

    assertEquals(
        firstStatistics,
        secondStatistics,
        "[TC-62] Данные статистики должны совпадать между повторными GET");
  }

  @Test
  void tc66_getStatisticWithCorrectAcceptHeader() {
    CreatedItem createdItem = createValidItem();

    ApiResponse response = statisticEndpoint.getByItemId(createdItem.id(), acceptJsonHeader());

    assertEquals(
        200, response.statusCode(), "[TC-66] Ожидался статус 200 с Accept: application/json");
    assertStatisticContract(firstObject(response.json(), "TC-66 ответ"), "TC-66");
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("ru.avito.qa.api.cases.HeaderScenarioProvider#getStatisticHeaderCases")
  void tc75to76_getStatisticWithMissingOrInvalidAccept(HeaderScenario scenario) {
    CreatedItem createdItem = createValidItem();

    ApiResponse response = statisticEndpoint.getByItemId(createdItem.id(), scenario.headers());

    TestAssertions.assertControlledStatus(response, scenario.caseId());
    if (response.statusCode() == 200) {
      assertStatisticContract(
          firstObject(response.json(), scenario.caseId() + " ответ"), scenario.caseId());
    }
  }
}
