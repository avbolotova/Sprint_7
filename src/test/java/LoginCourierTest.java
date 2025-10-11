import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginCourierTest {

    private List<Integer> courierIdsToDelete = new ArrayList<>();

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        courierIdsToDelete.clear();
    }

    public void createCourier(String login, String password, String firstName) {
        String loginJson = "{ \"login\": \"" + login + "\", \"password\": \"" + password + "\", \"firstName\": \"" + firstName + "\" }";

        Response createResponse =
                given()
                        .header("Content-type", "application/json")
                        .body(loginJson)
                        .when()
                        .post("/api/v1/courier");

        createResponse.then().assertThat()
                .statusCode(201)
                .and()
                .body("ok", equalTo(true));
    }

    public int getCourierId(String login, String password) {
        String loginJson = "{ \"login\": \"" + login + "\", \"password\": \"" + password + "\" }";

        Response loginResponse =
                given()
                        .header("Content-type", "application/json")
                        .body(loginJson)
                        .when()
                        .post("/api/v1/courier/login");

        return loginResponse.then().extract().path("id");
    }

    @After
    public void tearDown() {
        for (Integer id : courierIdsToDelete) {
            try {
                given()
                        .when()
                        .delete("/api/v1/courier/" + id)
                        .then()
                        .statusCode(200)
                        .and()
                        .body("ok", equalTo(true));
            } catch (Exception e) {
            }
        }
    }

    @Test
    @DisplayName("Авторизация курьера с валидными данными ")
    @Description("Positive test: endpoint /api/v1/courier/login")
    public void loginCourier(){
        createCourier("alpav2021", "1234", "alpav2021");

        Courier courier = new Courier("alpav2021", "1234");

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(courier.toJson())
                        .when()
                        .post("/api/v1/courier/login");

        response.then().assertThat()
                .statusCode(200)
                .and()
                .body("id", notNullValue());

        int id = response.then().extract().path("id");
        courierIdsToDelete.add(id);
    }

    @Test
    @DisplayName("Ожидание ошибки 404 при авторизация курьера с несуществующим пользователем")
    @Description("Negative test: endpoint /api/v1/courier/login")
    public void loginNonExistentCourier(){
        Courier courier = new Courier("alpav2025", "1234");

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(courier.toJson())
                        .when()
                        .post("/api/v1/courier/login");

        response.then().assertThat()
                .statusCode(404)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ожидание ошибки 404 при авторизация курьера без поля login")
    @Description("Negative test: endpoint /api/v1/courier/login")
    public void loginCourierWithoutLogin(){
        createCourier("alpav2026", "1234", "alpav2026");

        try {
        String password = "1234";
        String json = "{ \"password\": \"" + password + "\" }";

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post("/api/v1/courier/login");

        response.then().assertThat()
                .statusCode(404)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
        } finally {
            int id = getCourierId("alpav2026", "1234");
            courierIdsToDelete.add(id);
        }
    }

    @Test
    @DisplayName("Ожидание ошибки 404 при авторизация курьера без поля password")
    @Description("Negative test: endpoint /api/v1/courier/login")
    public void loginCourierWithoutPassword() {
        createCourier("alpav2027", "1234", "alpav2027");

        try {
            String login = "alpav2027";
            String json = "{ \"login\": \"" + login + "\" }";

            Response response =
                    given()
                            .header("Content-type", "application/json")
                            .and()
                            .body(json)
                            .when()
                            .post("/api/v1/courier/login");

            response.then().assertThat()
                    .statusCode(404)
                    .and()
                    .body("message", equalTo("Учетная запись не найдена"));
        } finally {
            int id = getCourierId("alpav2027", "1234");
            courierIdsToDelete.add(id);
        }
    }

    @Test
    @DisplayName("Ожидание ошибки 404 при авторизация курьера c полем firstName")
    @Description("Negative test: endpoint /api/v1/courier/login")
    public void loginCourierWithFirstName(){
        createCourier("alpav2028", "1234", "alpav2028");

        try {
            String login = "alpav2028";
            String firstName = "alpav2028";
            String json = "{ \"login\": \"" + login + "\", \"firstName\": \"" + firstName + "\" }";

            Response response =
                    given()
                            .header("Content-type", "application/json")
                            .and()
                            .body(json)
                            .when()
                            .post("/api/v1/courier/login");

            response.then().assertThat()
                    .statusCode(404)
                    .and()
                    .body("message", equalTo("Учетная запись не найдена"));
        } finally {
            int id = getCourierId("alpav2028", "1234");
            courierIdsToDelete.add(id);
        }
    }
}
