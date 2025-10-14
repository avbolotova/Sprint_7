package tests;

import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.Courier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.ApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateCourierTest {

    private static final Logger logger = Logger.getLogger(LoginCourierTest.class.getName());
    private List<Integer> courierIdsToDelete = new ArrayList<>();
    private ApiClient apiClient;
    private Faker faker;

    @Before
    public void setUp() {
        courierIdsToDelete.clear();
        apiClient = new ApiClient();
        faker = new Faker();
    }

    public int getCourierId(String login, String password) {
        Courier courier = new Courier(login, password);
        Response loginResponse = apiClient.loginCourier(courier);
        return loginResponse.then().extract().path("id");
    }

    @After
    public void tearDown() {
        for (Integer id : courierIdsToDelete) {
            try {
                apiClient.deleteCourier(id);
            } catch (Exception e) {
            }
        }
    }

    @Test
    @DisplayName("Создание курьера c валидными данными")
    @Description("Positive test: endpoint /api/v1/courier")
    public void createNewCourier() {
        String login = faker.name().username();
        String password = "1234";
        String firstName = faker.name().firstName();

        logger.info("Создание курьера с логином: " + login);

        Courier courier = new Courier(login, password, firstName);

        Response response = apiClient.createCourier(courier);

        try {
            response.then().assertThat()
                    .statusCode(SC_CREATED)
                    .and()
                    .body("ok", equalTo(true));

        } finally {
            int id = getCourierId(courier.getLogin(), courier.getPassword());
            courierIdsToDelete.add(id);
        }
    }

    @Test
    @DisplayName("Ожидание ошибки 409 при создании дубликата-курьера")
    @Description("Negative test: endpoint /api/v1/courier")
    public void createDublicateCourier() {

        String login = faker.name().username();
        String password = "1234";
        String firstName = faker.name().firstName();

        logger.info("Создание курьера с логином: " + login);

        Courier courier = new Courier(login, password, firstName);

        try {
            apiClient.createCourier(courier);
            Response response = apiClient.createCourier(courier);

            response.then().assertThat()
                    .statusCode(SC_CONFLICT)
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
    public void createNewCourierWithoutLogin() {

        Courier courier = new Courier();
        courier.setPassword("1234");
        courier.setFirstName(faker.name().firstName());

        logger.info("Создание курьера с логином: " + courier.getFirstName());

        Response response = apiClient.createCourier(courier);

        if (response.getStatusCode() == SC_CREATED) {
            try {
                int id = getCourierId(courier.getLogin(), courier.getPassword());
                courierIdsToDelete.add(id);
            } catch (Exception ignored) {
            }
            throw new AssertionError("Ожидался статус 400, но курьер был создан (статус 201).");
        }

        response.then().assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Ожидание ошибки 400 при создании курьера без поля password")
    @Description("Negative test: endpoint /api/v1/courier")
    public void createNewCourierWithoutPassword() {

        Courier courier = new Courier();
        courier.setLogin(faker.name().username());
        courier.setFirstName(faker.name().firstName());

        logger.info("Создание курьера с логином: " + courier.getLogin());

        Response response = apiClient.createCourier(courier);

        if (response.getStatusCode() == SC_CREATED) {
            try {
                int id = getCourierId(courier.getLogin(), courier.getPassword());
                courierIdsToDelete.add(id);
            } catch (Exception ignored) {
            }
            throw new AssertionError("Ожидался статус 400, но курьер был создан (статус 201).");
        }

        response.then().assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Ожидание ошибки 400 при создании курьера без поля first name")
    @Description("Negative test: endpoint /api/v1/courier")
    public void createNewCourierWithoutFirstName() {

        Courier courier = new Courier(faker.name().username(), "1234");

        logger.info("Создание курьера с логином: " + courier.getLogin());

        Response response = apiClient.createCourier(courier);

        if (response.getStatusCode() == SC_CREATED) {
            try {
                int id = getCourierId(courier.getLogin(), courier.getPassword());
                courierIdsToDelete.add(id);
            } catch (Exception ignored) {
            }
            throw new AssertionError("Ожидался статус 400, но курьер был создан (статус 201).");
        }

        response.then().assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Ожидание ошибки 400 при создании курьера без всех полей")
    @Description("Negative test: endpoint /api/v1/courier")
    public void createNewCourierWithoutAllFields() {
        Courier courier = new Courier();

        Response response = apiClient.createCourier(courier);

        response.then().assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}

