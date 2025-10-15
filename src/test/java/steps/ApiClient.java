package steps;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.Courier;
import models.OrderCancellation;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;

public class ApiClient {

    public static final String BASE_URI = "https://qa-scooter.praktikum-services.ru";
    public static final String COURIER_LOGIN_ENDPOINT = "/api/v1/courier/login";
    public static final String COURIER_CREATE_ENDPOINT = "/api/v1/courier";
    public static final String COURIER_DELETE_ENDPOINT = "/api/v1/courier/";
    public static final String ORDER_ENDPOINT = "/api/v1/orders";
    public static final String ORDER_CANCEL_ENDPOINT = "/api/v1/orders/cancel";

    public static RequestSpecification baseSpec = new RequestSpecBuilder()
            .setBaseUri(BASE_URI)
            .setContentType(ContentType.JSON)
            .build();

    public Response createCourier(Courier courier) {
        step("POST-запрос создания курьера " + courier.getLogin());
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post(BASE_URI + COURIER_CREATE_ENDPOINT);
    }

    public Response loginCourier(Courier courier) {
        step("POST-запрос логина курьера " + courier.getLogin());
        return given()
                .spec(baseSpec)
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post(COURIER_LOGIN_ENDPOINT);
    }

    public Response deleteCourier(int id) {
        step("DELETE-запрос удаления курьера по ID: " + id);
        return given()
                .spec(baseSpec)
                .when()
                .delete(COURIER_DELETE_ENDPOINT + id);
    }

    public Response createOrder(Object order) {
        step("POST-запрос создания заказа" );
        return given()
                .spec(baseSpec)
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post(ORDER_ENDPOINT);
    }

    public Response cancelOrder(int track) {
        step("PUT-запрос отмены заказа " + track);
        OrderCancellation cancellation = new OrderCancellation(track);
        return given()
                .spec(baseSpec)
                .header("Content-type", "application/json")
                .body(cancellation)
                .when()
                .put(ORDER_CANCEL_ENDPOINT);
    }

    public Response getOrdersList() {
        step("GET-запрос получение списка заказов");
        return given()
                .spec(baseSpec)
                .when()
                .get(ORDER_ENDPOINT);
    }
}
