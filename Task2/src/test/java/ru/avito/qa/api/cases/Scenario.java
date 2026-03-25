package ru.avito.qa.api.cases;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.function.Supplier;

public record Scenario(
    String caseId, String title, Supplier<ObjectNode> payloadSupplier, int expectedStatus) {

  @Override
  public String toString() {
    return caseId + " - " + title;
  }
}
