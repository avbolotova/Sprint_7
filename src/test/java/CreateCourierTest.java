import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreateCourierTest {

    private List<Integer> courierIdsToDelete = new ArrayList<>();

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        courierIdsToDelete.clear();
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
    @DisplayName("Создание курьера c валидными данными")
    @Description("Positive test: endpoint /api/v1/courier")
    public void createNewCourier(){
        Courier courier = new Courier("alpav9190", "1234", "alpav9190");

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(courier.toJson())
                        .when()
                        .post("/api/v1/courier");
        response.then().assertThat()
                .statusCode(201)
                .and()
                .body("ok", equalTo(true));

        int id = getCourierId(courier.getLogin(), courier.getPassword());
        courierIdsToDelete.add(id);
    }

    @Test
    @DisplayName("Ожидание ошибки 409 при создании дубликата-курьера")
    @Description("Negative test: endpoint /api/v1/courier")
    public void createDublicateCourier(){
        Courier courier = new Courier("alpav2552", "1234", "alpav2552");

        try {
            given()
                    .header("Content-type", "application/json")
                    .body(courier.toJson())
                    .when()
                    .post("/api/v1/courier");

            Response response =
                    given()
                            .header("Content-type", "application/json")
                            .and()
                            .body(courier.toJson())
                            .when()
                            .post("/api/v1/courier");
            response.then().assertThat()
                    .statusCode(409)
                    .and()
                    .body("message", equalTo("Этот логин уже используется"));
        } finally {
            int id = getCourierId(courier.getLogin(), courier.getPassword());
            courierIdsToDelete.add(id);
        }
    }


    @Test
    @DisplayName("Ожидание ошибки 400 при создании курьера без поля login")
    @Description("Negative test: endpoint /api/v1/courier")
    public void createNewCourierWithoutLogin(){
        String firstName = "alpav9192";
        String password = "1234";
        String json = "{ \"firstName\": \"" + firstName + "\", \"password\": \"" + password + "\" }";

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post("/api/v1/courier");

        if (response.getStatusCode() == 201) {
            try {
                int id = getCourierId(firstName, password);
                courierIdsToDelete.add(id);
            } catch (Exception ignored) {}
            throw new AssertionError("Ожидался статус 400, но курьер был создан (статус 201).");
        }

        response.then().assertThat()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Ожидание ошибки 400 при создании курьера без поля password")
    @Description("Negative test: endpoint /api/v1/courier")
    public void createNewCourierWithoutPassword(){
        String login = "alpav9193";
        String firstName = "alpav9193";
        String json = "{ \"login\": \"" + login + "\", \"firstName\": \"" + firstName + "\" }";

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post("/api/v1/courier");

        if (response.getStatusCode() == 201) {
            try {
                int id = getCourierId(login, firstName);
                courierIdsToDelete.add(id);
            } catch (Exception ignored) {}
            throw new AssertionError("Ожидался статус 400, но курьер был создан (статус 201).");
        }

        response.then().assertThat()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Ожидание ошибки 400 при создании курьера без поля first name")
    @Description("Negative test: endpoint /api/v1/courier")
    public void createNewCourierWithoutFirstName() {
        Courier courier = new Courier("alpav9194", "1234");

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(courier.toJson())
                        .when()
                        .post("/api/v1/courier");

        if (response.getStatusCode() == 201) {
            try {
                int id = getCourierId(courier.getLogin(), courier.getPassword());
                courierIdsToDelete.add(id);
            } catch (Exception ignored) {}
            throw new AssertionError("Ожидался статус 400, но курьер был создан (статус 201).");
        }

        response.then().assertThat()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Ожидание ошибки 400 при создании курьера без всех полей")
    @Description("Negative test: endpoint /api/v1/courier")
    public void createNewCourierWithoutAllFields() {
        String json = "{ \"login\": \""  + "\", \"password\": \""  + "\", \"firstName\": \""  + "\" }";

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post("/api/v1/courier");
        response.then().assertThat()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}

