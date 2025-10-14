package tests;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import steps.ApiClient;

import java.util.Arrays;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrdersTest {

    private ApiClient apiClient;
    private final String[] colors;
    private final String testName;
    private int track;
    private boolean orderCreated;

    public CreateOrdersTest(String[] colors, String testName) {
        this.colors = colors;
        this.testName = testName;
    }

    @Parameterized.Parameters(name = "{index}: {1}")
    public static Object[][] getData() {
        return new Object[][] {
                {new String[]{"BLACK"}, "цвет BLACK"},
                {new String[]{"GREY"}, "цвет GREY"},
                {new String[]{"BLACK", "GREY"}, "цвета BLACK и GREY"},
                {new String[]{}, "без указания цвета"}
        };
    }

    @Before
    public void setUp() {
        apiClient = new ApiClient();
        orderCreated = false;
        Allure.getLifecycle().updateTestCase(testResult ->
                testResult.setName("Параметризованный тест создания заказа - " + testName)
        );
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
        Order order = new Order();
        order.setFirstName("Sakura");
        order.setLastName("Haruno");
        order.setAddress("Konoha, 123 apt.");
        order.setMetroStation(6);
        order.setPhone("+7 900 123 45 67");
        order.setRentTime(3);
        order.setDeliveryDate("2023-10-06");
        order.setComment("Please deliver quickly");

        if (colors.length > 0) {
            order.setColor(Arrays.asList(colors));
        }

        Response response = apiClient.createOrder(order);

        orderCreated = true;

        response.then().assertThat()
                .statusCode(SC_CREATED)
                .and()
                .body("track", notNullValue());

        track = response.jsonPath().getInt("track");
    }
}
