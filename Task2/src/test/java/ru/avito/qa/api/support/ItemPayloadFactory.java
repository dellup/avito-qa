package ru.avito.qa.api.support;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.concurrent.ThreadLocalRandom;

public final class ItemPayloadFactory {

  private static final int MIN_SELLER_ID = 111111;
  private static final int MAX_SELLER_ID = 1_000_000;

  private ItemPayloadFactory() {}

  public static int randomSellerId() {
    return ThreadLocalRandom.current().nextInt(MIN_SELLER_ID, MAX_SELLER_ID);
  }

  public static ObjectNode validPayload() {
    return validPayload(randomSellerId());
  }

  public static ObjectNode validPayload(int sellerId) {
    ObjectNode root = JsonNodeFactory.instance.objectNode();
    root.put("sellerID", sellerId);
    root.put("name", "Test item");
    root.put("price", 1000);
    root.set("statistics", statistics(10, 100, 5));
    return root;
  }

  public static ObjectNode statistics(int likes, int viewCount, int contacts) {
    ObjectNode statistics = JsonNodeFactory.instance.objectNode();
    statistics.put("likes", likes);
    statistics.put("viewCount", viewCount);
    statistics.put("contacts", contacts);
    return statistics;
  }
}
