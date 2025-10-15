package tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import steps.ApiClient;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.notNullValue;

public class ListOrdersTest {

    private ApiClient apiClient;

    @Before
    public void setUp() {
        apiClient = new ApiClient();
    }

    @Test
    @DisplayName("Получение списка заказов без параметров")
    @Description("Positive test: endpoint /api/v1/orders")
    public void getListOrdersWithoutParameters() {

        Response response = apiClient.getOrdersList();

        response.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("orders", notNullValue())
                .body("pageInfo", notNullValue())
                .body("availableStations", notNullValue());
    }

    @Test
    @DisplayName("Проверка структуры ответа при получении списка заказов")
    @Description("Positive test: endpoint /api/v1/orders")
    public void checkOrdersListResponseStructure() {
        Response response = apiClient.getOrdersList();

        response.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("orders", notNullValue())
                .body("pageInfo", notNullValue())
                .body("availableStations", notNullValue())
                .body("orders[0].id", notNullValue())
                .body("orders[0].firstName", notNullValue())
                .body("orders[0].lastName", notNullValue())
                .body("orders[0].address", notNullValue())
                .body("orders[0].metroStation", notNullValue())
                .body("orders[0].phone", notNullValue())
                .body("orders[0].rentTime", notNullValue())
                .body("orders[0].deliveryDate", notNullValue())
                .body("orders[0].track", notNullValue())
                .body("orders[0].color", notNullValue())
                .body("orders[0].comment", notNullValue())
                .body("orders[0].createdAt", notNullValue())
                .body("orders[0].updatedAt", notNullValue())
                .body("orders[0].status", notNullValue())
                .body("pageInfo.page", notNullValue())
                .body("pageInfo.total", notNullValue())
                .body("pageInfo.limit", notNullValue());
    }
}
