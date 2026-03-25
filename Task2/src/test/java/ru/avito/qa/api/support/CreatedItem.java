package ru.avito.qa.api.support;

import com.fasterxml.jackson.databind.node.ObjectNode;

public record CreatedItem(String id, int sellerId, ObjectNode requestPayload) {
}
