package ru.avito.qa.api.tests;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.avito.qa.api.cases.Scenario;
import ru.avito.qa.api.cases.HeaderScenario;
import ru.avito.qa.api.client.ApiResponse;
import ru.avito.qa.api.support.ApiTestBase;
import ru.avito.qa.api.support.ItemPayloadFactory;
import ru.avito.qa.api.support.JsonSupport;
import ru.avito.qa.api.support.TestAssertions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateItemApiTest extends ApiTestBase {

    @Test
    void tc01_successfulCreateWithValidData() {
        ObjectNode payload = ItemPayloadFactory.validPayload();

        ApiResponse response = createItemEndpoint.create(payload, jsonHeaders());

        assertEquals(200, response.statusCode(), "[TC-01] Ожидался статус 200 для валидного тела запроса");
        ObjectNode createdItem = firstObject(response.json(), "TC-01 ответ");
        assertItemContract(createdItem, "TC-01");
        assertRequestAndResponseMatch(payload, createdItem, "TC-01");
    }

    @Test
    void tc02_sellerIdMappingFromSellerID() {
        ObjectNode payload = ItemPayloadFactory.validPayload();
        int sellerIdFromRequest = payload.get("sellerID").asInt();

        ApiResponse response = createItemEndpoint.create(payload, jsonHeaders());

        assertEquals(200, response.statusCode(), "[TC-02] Ожидался статус 200");
        ObjectNode createdItem = firstObject(response.json(), "TC-02 ответ");
        assertTrue(createdItem.has("sellerId") && createdItem.get("sellerId").isNumber(),
                "[TC-02] В ответе должно быть числовое поле sellerId");
        assertEquals(sellerIdFromRequest, createdItem.get("sellerId").asInt(),
                "[TC-02] sellerId в ответе должен совпадать с sellerID из запроса");
    }

    @Test
    void tc03_successResponseStructureOnCreate() {
        ObjectNode payload = ItemPayloadFactory.validPayload();

        ApiResponse response = createItemEndpoint.create(payload, jsonHeaders());

        assertEquals(200, response.statusCode(), "[TC-03] Ожидался статус 200");
        assertItemContract(firstObject(response.json(), "TC-03 ответ"), "TC-03");
    }

    @Test
    void tc04_twoCreatesWithSamePayloadShouldHaveDifferentIds() {
        ObjectNode payload = ItemPayloadFactory.validPayload();

        ApiResponse firstResponse = createItemEndpoint.create(payload, jsonHeaders());
        ApiResponse secondResponse = createItemEndpoint.create(payload, jsonHeaders());

        assertEquals(200, firstResponse.statusCode(), "[TC-04] Первый POST должен вернуть 200");
        assertEquals(200, secondResponse.statusCode(), "[TC-04] Второй POST должен вернуть 200");

        String firstId = requiredText(firstObject(firstResponse.json(), "TC-04 первый ответ"), "id", "TC-04");
        String secondId = requiredText(firstObject(secondResponse.json(), "TC-04 второй ответ"), "id", "TC-04");

        assertNotEquals(firstId, secondId, "[TC-04] Для каждой операции создания id должен быть уникальным");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("ru.avito.qa.api.cases.DataProvider#strictCreateCases")
    void strictCreateValidationCases(Scenario scenario) {
        ObjectNode payload = scenario.payloadSupplier().get();

        ApiResponse response = createItemEndpoint.create(payload, jsonHeaders());

        assertEquals(scenario.expectedStatus(), response.statusCode(),
                () -> "[" + scenario.caseId() + "] Неожиданный статус. Тело: " + response.body());

        if (scenario.expectedStatus() == 200) {
            ObjectNode createdItem = firstObject(response.json(), scenario.caseId() + " ответ");
            assertItemContract(createdItem, scenario.caseId());
            assertRequestAndResponseMatch(payload, createdItem, scenario.caseId());
        } else {
            TestAssertions.assertErrorPayloadPresent(response, scenario.caseId());
        }
    }

    @Test
    void tc63_createWithCorrectContentTypeAndAcceptHeaders() {
        ObjectNode payload = ItemPayloadFactory.validPayload();

        ApiResponse response = createItemEndpoint.create(payload, jsonHeaders());

        assertEquals(200, response.statusCode(), "[TC-63] Ожидался статус 200 с валидными заголовками");
        ObjectNode createdItem = firstObject(response.json(), "TC-63 ответ");
        assertItemContract(createdItem, "TC-63");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("ru.avito.qa.api.cases.HeaderScenarioProvider#createPostHeaderCases")
    void tc67to70_createWithOptionalOrInvalidHeaders(HeaderScenario scenario) {
        ObjectNode payload = ItemPayloadFactory.validPayload();
        String rawBody = JsonSupport.toJson(payload);

        ApiResponse response = createItemEndpoint.createRaw(rawBody, scenario.headers());

        TestAssertions.assertControlledStatus(response, scenario.caseId());
        assertTrue(response.body() == null || !response.body().isBlank(),
                () -> "[" + scenario.caseId() + "] Для контролируемого поведения тело ответа должно присутствовать");
    }

    private void assertRequestAndResponseMatch(ObjectNode request, ObjectNode response, String caseId) {
        assertEquals(request.get("name").asText(), response.get("name").asText(),
                "[" + caseId + "] name в ответе должен совпадать с запросом");
        assertEquals(request.get("price").asInt(), response.get("price").asInt(),
                "[" + caseId + "] price в ответе должен совпадать с запросом");
        assertEquals(request.get("sellerID").asInt(), response.get("sellerId").asInt(),
                "[" + caseId + "] sellerId в ответе должен совпадать с sellerID из запроса");

        ObjectNode requestStatistics = (ObjectNode) request.get("statistics");
        ObjectNode responseStatistics = (ObjectNode) response.get("statistics");
        assertEquals(requestStatistics.get("likes").asInt(), responseStatistics.get("likes").asInt(),
                "[" + caseId + "] statistics.likes должен совпадать с запросом");
        assertEquals(requestStatistics.get("viewCount").asInt(), responseStatistics.get("viewCount").asInt(),
                "[" + caseId + "] statistics.viewCount должен совпадать с запросом");
        assertEquals(requestStatistics.get("contacts").asInt(), responseStatistics.get("contacts").asInt(),
                "[" + caseId + "] statistics.contacts должен совпадать с запросом");
    }
}
