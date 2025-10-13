import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrdersTest {

    private ApiClient apiClient;
    private final String[] colors;
    private int track;
    private boolean orderCreated;

    public CreateOrdersTest(String[] colors, String testName) {
        this.colors = colors;
    }

    @Parameterized.Parameters(name = "{index}: {1}")
    public static Object[][] getData() {
        return new Object[][] {
                {new String[]{"BLACK"}, "Создание заказа с цветом BLACK"},
                {new String[]{"GREY"}, "Создание заказа с цветом GREY"},
                {new String[]{"BLACK", "GREY"}, "Создание заказа с цветами BLACK и GREY"},
                {new String[]{}, "Создание заказа без указания цвета"}
        };
    }

    @Before
    public void setUp() {
        apiClient = new ApiClient();
        orderCreated = false;
    }

    @After
    public void tearDown() {
        if (orderCreated) {
            apiClient.cancelOrder(track);
        }
    }

    @Test
    @DisplayName("Параметризованный тест создания заказа")
    @Description("Positive test: endpoint /api/v1/orders")
    public void createOrderWithColors() {
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

        Response response = apiClient.createOrder(orderJson);

        response.then().assertThat()
                .statusCode(SC_CREATED)
                .and()
                .body("track", notNullValue());

        track = response.jsonPath().getInt("track");
        orderCreated = true;
    }
}
