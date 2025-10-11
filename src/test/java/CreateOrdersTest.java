import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreateOrdersTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    @DisplayName("Создание заказа с цветом BLACK")
    @Description("Positive test: endpoint /api/v1/orders")
    public void createOrderWithBlackColor() {
        createOrderWithColors(new String[]{"BLACK"});
    }

    @Test
    @DisplayName("Создание заказа с цветом GREY")
    @Description("Positive test: endpoint /api/v1/orders")
    public void createOrderWithGreyColor() {
        createOrderWithColors(new String[]{"GREY"});
    }

    @Test
    @DisplayName("Создание заказа с цветами BLACK и GREY")
    @Description("Positive test: endpoint /api/v1/orders")
    public void createOrderWithBlackAndGreyColors() {
        createOrderWithColors(new String[]{"BLACK", "GREY"});
    }

    @Test
    @DisplayName("Создание заказа без указания цвета")
    @Description("Positive test: endpoint /api/v1/orders")
    public void createOrderWithoutColor() {
        createOrderWithColors(new String[]{});
    }

    private void createOrderWithColors(String[] colors) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{ ")
                .append("\"firstName\": \"Sakura\", ")
                .append("\"lastName\": \"Haruno\", ")
                .append("\"address\": \"Konoha, 123 apt.\", ")
                .append("\"metroStation\": 6, ")
                .append("\"phone\": \"+7 900 123 45 67\", ")
                .append("\"rentTime\": 3, ")
                .append("\"deliveryDate\": \"2023-10-06\", ")
                .append("\"comment\": \"Please deliver quickly\"");

        if (colors.length > 0) {
            String colorArray = "[\"" + String.join("\",\"", colors) + "\"]";
            jsonBuilder.append(", \"color\": ").append(colorArray);
        }

        jsonBuilder.append(" }");

        String orderJson = jsonBuilder.toString();

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(orderJson)
                        .when()
                        .post("/api/v1/orders");

        response.then().assertThat()
                .statusCode(201)
                .and()
                .body("track", notNullValue());
    }
}
