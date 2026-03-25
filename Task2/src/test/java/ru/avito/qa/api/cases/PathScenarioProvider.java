package ru.avito.qa.api.cases;

import java.util.stream.Stream;

public final class PathScenarioProvider {

    private PathScenarioProvider() {
    }

    public static Stream<PathScenario> invalidItemIdCases() {
        return Stream.of(
                new PathScenario("TC-44", "Получение объявления с пустым идентификатором", "", 400),
                new PathScenario("TC-45", "Получение объявления с невалидным форматом идентификатора (пробелы)", "   ", 400),
                new PathScenario("TC-46", "Получение объявления с невалидным форматом идентификатора (спецсимволы)", "!@#$%^&*", 400)
        );
    }

    public static Stream<PathScenario> invalidSellerIdCases() {
        return Stream.of(
                new PathScenario("TC-51", "Получение объявлений с пустым sellerID", "", 400),
                new PathScenario("TC-52", "Получение объявлений с невалидным типом sellerID", "boba", 400),
                new PathScenario("TC-53", "Получение объявлений с sellerID = 0", "0", 400),
                new PathScenario("TC-54", "Получение объявлений с отрицательным sellerID", "-1", 400)
        );
    }

    public static Stream<PathScenario> invalidStatisticIdCases() {
        return Stream.of(
                new PathScenario("TC-60", "Получение статистики с пустым идентификатором", "", 400),
                new PathScenario("TC-61", "Получение статистики с невалидным форматом идентификатора", "boba", 400)
        );
    }
}
