package ru.avito.qa.api.cases;

public record PathScenario(String caseId, String title, String pathValue, int expectedStatus) {

    @Override
    public String toString() {
        return caseId + " - " + title;
    }
}
