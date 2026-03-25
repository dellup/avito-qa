package ru.avito.qa.api.cases;

import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import ru.avito.qa.api.support.ItemPayloadFactory;

public final class DataProvider {

  private DataProvider() {}

  public static Stream<Scenario> strictCreateCases() {
    return Stream.of(
        scenario(
            "TC-05",
            "Создание объявления без обязательного поля sellerId",
            payload -> payload.remove("sellerID"),
            400),
        scenario(
            "TC-06",
            "Создание объявления без обязательного поля name",
            payload -> payload.remove("name"),
            400),
        scenario(
            "TC-07",
            "Создание объявления без обязательного поля price",
            payload -> payload.remove("price"),
            400),
        scenario(
            "TC-08",
            "Создание объявления без обязательного поля statistics",
            payload -> payload.remove("statistics"),
            400),
        scenario(
            "TC-09",
            "Создание объявления без обязательного поля statistics.likes",
            payload -> statistics(payload).remove("likes"),
            400),
        scenario(
            "TC-10",
            "Создание объявления без обязательного поля statistics.viewCount",
            payload -> statistics(payload).remove("viewCount"),
            400),
        scenario(
            "TC-11",
            "Создание объявления без обязательного поля statistics.contacts",
            payload -> statistics(payload).remove("contacts"),
            400),
        scenario(
            "TC-12",
            "Создание объявления с пустым JSON",
            JsonNodeFactory.instance::objectNode,
            400),
        scenario(
            "TC-13",
            "Создание объявления с пустым объектом statistics",
            payload -> payload.set("statistics", JsonNodeFactory.instance.objectNode()),
            400),
        scenario(
            "TC-14",
            "Создание объявления с невалидным типом поля sellerID (строка)",
            payload -> payload.put("sellerID", "invalid"),
            400),
        scenario(
            "TC-15",
            "Создание объявления с невалидным типом поля sellerID (дробное число)",
            payload -> payload.put("sellerID", 10.5),
            400),
        scenario(
            "TC-16",
            "Создание объявления с невалидным типом поля name (число)",
            payload -> payload.put("name", 12345),
            400),
        scenario(
            "TC-17",
            "Создание объявления с невалидным типом поля price (строка)",
            payload -> payload.put("price", "1000"),
            400),
        scenario(
            "TC-18",
            "Создание объявления с невалидным типом поля statistics (строка)",
            payload -> payload.put("statistics", "invalid"),
            400),
        scenario(
            "TC-19",
            "Создание объявления с невалидным типом поля statistics.likes (строка)",
            payload -> statistics(payload).put("likes", "invalid"),
            400),
        scenario(
            "TC-20",
            "Создание объявления с невалидным типом поля statistics.viewCount (строка)",
            payload -> statistics(payload).put("viewCount", "invalid"),
            400),
        scenario(
            "TC-21",
            "Создание объявления с невалидным типом поля statistics.contacts (строка)",
            payload -> statistics(payload).put("contacts", "invalid"),
            400),
        scenario(
            "TC-22",
            "Создание объявления с null в поле sellerID",
            payload -> payload.putNull("sellerID"),
            400),
        scenario(
            "TC-23",
            "Создание объявления с null в поле name",
            payload -> payload.putNull("name"),
            400),
        scenario(
            "TC-24",
            "Создание объявления с null в поле price",
            payload -> payload.putNull("price"),
            400),
        scenario(
            "TC-25",
            "Создание объявления с null в поле statistics",
            payload -> payload.putNull("statistics"),
            400),
        scenario(
            "TC-26",
            "Создание объявления с пустой строкой в поле name",
            payload -> payload.put("name", ""),
            400),
        scenario(
            "TC-27",
            "Создание объявления со строкой из пробелов в поле name",
            payload -> payload.put("name", "   "),
            400),
        scenario("TC-28", "Создание объявления с ценой 0", payload -> payload.put("price", 0), 200),
        scenario(
            "TC-29",
            "Создание объявления с отрицательной ценой",
            payload -> payload.put("price", -1),
            400),
        scenario(
            "TC-30",
            "Создание объявления с sellerID = 0",
            payload -> payload.put("sellerID", 0),
            400),
        scenario(
            "TC-31",
            "Создание объявления с нулевым значением statistics.likes",
            payload -> statistics(payload).put("likes", 0),
            400),
        scenario(
            "TC-32",
            "Создание объявления с нулевым значением statistics.viewCount",
            payload -> statistics(payload).put("viewCount", 0),
            400),
        scenario(
            "TC-33",
            "Создание объявления с нулевым значением statistics.contacts",
            payload -> statistics(payload).put("contacts", 0),
            200),
        scenario(
            "TC-34",
            "Создание объявления с отрицательным sellerID",
            payload -> payload.put("sellerID", -1),
            400),
        scenario(
            "TC-35A",
            "Создание объявления с отрицательным значением statistics.likes",
            payload -> statistics(payload).put("likes", -1),
            400),
        scenario(
            "TC-35B",
            "Создание объявления с отрицательным значением statistics.viewCount",
            payload -> statistics(payload).put("viewCount", -1),
            400),
        scenario(
            "TC-36",
            "Создание объявления с отрицательным значением statistics.contacts",
            payload -> statistics(payload).put("contacts", -1),
            400),
        scenario(
            "TC-37",
            "Создание объявления с очень большим значением price",
            payload ->
                payload.set(
                    "price",
                    new BigIntegerNode(
                        new BigInteger(
                            "98765434567890876543456789765434567898765456787654567654"))),
            400),
        scenario(
            "TC-38",
            "Создание объявления с очень длинным значением name",
            payload -> payload.put("name", "X".repeat(5000)),
            400),
        scenario(
            "TC-39",
            "Создание объявления с дополнительным неизвестным полем верхнего уровня",
            payload -> payload.put("unknownField", "unexpected"),
            400),
        scenario(
            "TC-40",
            "Создание объявления с дополнительным полем внутри statistics",
            payload -> statistics(payload).put("unknownStatField", 100),
            400));
  }

  private static Scenario scenario(
      String caseId, String title, Consumer<ObjectNode> mutator, int expectedStatus) {
    Supplier<ObjectNode> payloadSupplier =
        () -> {
          ObjectNode payload = ItemPayloadFactory.validPayload();
          mutator.accept(payload);
          return payload;
        };
    return new Scenario(caseId, title, payloadSupplier, expectedStatus);
  }

  private static Scenario scenario(
      String caseId, String title, Supplier<ObjectNode> payloadSupplier, int expectedStatus) {
    return new Scenario(caseId, title, payloadSupplier, expectedStatus);
  }

  private static ObjectNode statistics(ObjectNode payload) {
    return (ObjectNode) payload.get("statistics");
  }
}
