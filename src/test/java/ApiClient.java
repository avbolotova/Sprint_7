import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

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
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post(BASE_URI + COURIER_CREATE_ENDPOINT);
    }

    public Response loginCourier(Courier courier) {
        return given()
                .spec(baseSpec)
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post(COURIER_LOGIN_ENDPOINT);
    }

    public Response deleteCourier(int id) {
        return given()
                .spec(baseSpec)
                .when()
                .delete(COURIER_DELETE_ENDPOINT + id);
    }

    public Response createOrder(String orderJson) {
        return given()
                .spec(baseSpec)
                .header("Content-type", "application/json")
                .body(orderJson)
                .when()
                .post(ORDER_ENDPOINT);
    }

    public Response cancelOrder(int track) {
        return given()
                .spec(baseSpec)
                .header("Content-type", "application/json")
                .body("{\"track\": " + track + "}")
                .when()
                .put(ORDER_CANCEL_ENDPOINT);
    }
    public Response getOrdersList() {
        return given()
                .spec(baseSpec)
                .when()
                .get(ORDER_ENDPOINT);
    }
}
