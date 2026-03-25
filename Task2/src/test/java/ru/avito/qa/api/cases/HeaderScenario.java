package ru.avito.qa.api.cases;

import java.util.Map;

public record HeaderScenario(String caseId, String title, Map<String, String> headers) {

    @Override
    public String toString() {
        return caseId + " - " + title;
    }
}
