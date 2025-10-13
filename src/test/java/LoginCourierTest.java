import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginCourierTest {

    private static final Logger logger = Logger.getLogger(LoginCourierTest.class.getName());
    private List<Integer> courierIdsToDelete = new ArrayList<>();
    private ApiClient apiClient;

    private String validLogin;
    private String validPassword;
    private String validFirstName;

    private static Faker faker = new Faker();

    @Before
    public void setUp() {
        courierIdsToDelete.clear();
        apiClient = new ApiClient();
        validLogin = faker.name().username() + "_" + UUID.randomUUID().toString().substring(0, 8);
        validPassword = "1234";
        validFirstName = faker.name().firstName();
        logger.info("Создание курьера с логином: " + validLogin);
        createCourier(validLogin, validPassword, validFirstName);
    }

    public void createCourier(String login, String password, String firstName) {
        Courier courier = new Courier(login, password, firstName);

        Response createResponse = apiClient.createCourier(courier);
        createResponse.then().assertThat()
                .statusCode(SC_CREATED)
                .and()
                .body("ok", equalTo(true));
        logger.info("Курьер успешно создан: логин=" + login);
    }

    public int getCourierId(String login, String password) {
        Courier courier = new Courier(login, password);
        Response loginResponse = apiClient.loginCourier(courier);
        Integer id = loginResponse.then().extract().path("id");
        if (id == null) {
            throw new IllegalStateException("Не удалось получить ID курьера. Ответ сервера: " + loginResponse.asString());
        }
        return id;
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
    @DisplayName("Авторизация курьера с валидными данными ")
    @Description("Positive test: endpoint /api/v1/courier/login")
    public void loginCourier(){
        Courier courier = new Courier(validLogin, validPassword);

        try {
            Response response = apiClient.loginCourier(courier);

            response.then().assertThat()
                    .statusCode(SC_OK)
                    .and()
                    .body("id", notNullValue());

        } finally {
            int id = getCourierId(validLogin, validPassword);
            courierIdsToDelete.add(id);
            }

    }

    @Test
    @DisplayName("Ожидание ошибки 404 при авторизация курьера с несуществующим логином")
    @Description("Negative test: endpoint /api/v1/courier/login")
    public void loginNonExistentCourier(){
        Courier courier = new Courier("alpav2022", validPassword);

        try {
            Response response = apiClient.loginCourier(courier);

            response.then().assertThat()
                    .statusCode(SC_NOT_FOUND)
                    .and()
                    .body("message", equalTo("Учетная запись не найдена"));

        } finally {
            int id = getCourierId(validLogin, validPassword);
            courierIdsToDelete.add(id);
        }
    }

    @Test
    @DisplayName("Ожидание ошибки 404 при авторизация курьера с несуществующим паролем")
    @Description("Negative test: endpoint /api/v1/courier/login")
    public void passwordNonExistentCourier(){
        Courier courier = new Courier(validLogin, "1478");
        try {
            Response response = apiClient.loginCourier(courier);

        response.then().assertThat()
                .statusCode(SC_NOT_FOUND)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));

        } finally {
            int id = getCourierId(validLogin, validPassword);
            courierIdsToDelete.add(id);
        }
    }

    @Test
    @DisplayName("Ожидание ошибки 400 при авторизация курьера без поля login")
    @Description("Negative test: endpoint /api/v1/courier/login")
    public void loginCourierWithoutLogin() {
        Courier courier = new Courier(validPassword);
        try {
            Response response = apiClient.loginCourier(courier);

            response.then().assertThat()
                    .statusCode(SC_BAD_REQUEST)
                    .and()
                    .body("message", equalTo("Учетная запись не найдена"));

        } finally {
            int id = getCourierId(validLogin, validPassword);
            courierIdsToDelete.add(id);
        }

    }

    @Test
    @DisplayName("Ожидание ошибки 400 при авторизация курьера без поля password")
    @Description("Negative test: endpoint /api/v1/courier/login")
    public void loginCourierWithoutPassword() {
        Courier courier = new Courier(validLogin);

        try {
            Response response = apiClient.loginCourier(courier);

            response.then().assertThat()
                    .statusCode(SC_BAD_REQUEST)
                    .and()
                    .body("message", equalTo("Учетная запись не найдена"));
        } finally {
            int id = getCourierId(validLogin, validPassword);
            courierIdsToDelete.add(id);
        }
    }

    @Test
    @DisplayName("Ожидание ошибки 404 при авторизация курьера c полем firstName")
    @Description("Negative test: endpoint /api/v1/courier/login")
    public void loginCourierWithFirstName(){
        Courier courier = new Courier(validLogin, validFirstName);

        try {
            Response response = apiClient.loginCourier(courier);

            response.then().assertThat()
                    .statusCode(SC_NOT_FOUND)
                    .and()
                    .body("message", equalTo("Учетная запись не найдена"));
        } finally {
            int id = getCourierId(validLogin, validPassword);
            courierIdsToDelete.add(id);
        }
    }
}
