package ru.avito.qa.api.cases;

import java.util.Map;
import java.util.stream.Stream;

public final class HeaderScenarioProvider {

  private HeaderScenarioProvider() {}

  public static Stream<HeaderScenario> createPostHeaderCases() {
    return Stream.of(
        new HeaderScenario(
            "TC-67",
            "Создание объявления без заголовка Content-Type",
            Map.of("Accept", "application/json")),
        new HeaderScenario(
            "TC-68",
            "Создание объявления с некорректным заголовком Content-Type",
            Map.of("Content-Type", "text/plain", "Accept", "application/json")),
        new HeaderScenario(
            "TC-69",
            "Создание объявления без заголовка Accept",
            Map.of("Content-Type", "application/json")),
        new HeaderScenario(
            "TC-70",
            "Создание объявления с некорректным заголовком Accept",
            Map.of("Content-Type", "application/json", "Accept", "text/plain")));
  }

  public static Stream<HeaderScenario> getItemHeaderCases() {
    return Stream.of(
        new HeaderScenario("TC-71", "Получение объявления по id без заголовка Accept", Map.of()),
        new HeaderScenario(
            "TC-72",
            "Получение объявления по id с некорректным заголовком Accept",
            Map.of("Accept", "text/plain")));
  }

  public static Stream<HeaderScenario> getSellerItemsHeaderCases() {
    return Stream.of(
        new HeaderScenario("TC-73", "Получение объявлений продавца без заголовка Accept", Map.of()),
        new HeaderScenario(
            "TC-74",
            "Получение объявлений продавца с некорректным заголовком Accept",
            Map.of("Accept", "text/plain")));
  }

  public static Stream<HeaderScenario> getStatisticHeaderCases() {
    return Stream.of(
        new HeaderScenario("TC-75", "Получение статистики без заголовка Accept", Map.of()),
        new HeaderScenario(
            "TC-76",
            "Получение статистики с некорректным заголовком Accept",
            Map.of("Accept", "text/plain")));
  }
}
