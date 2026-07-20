import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import pojo.Courier;
import steps.CourierSteps;
import static org.hamcrest.Matchers.*;

@Execution(ExecutionMode.CONCURRENT)
@DisplayName("Функциональность: Логин курьера")
public class LoginCourierTest {

    private String login;
    private String password;
    private Courier courier;
    private static final String INSUFFICIENT_DATA_MESSAGE = "Недостаточно данных для входа";
    private static final String NOT_FOUND_COURIER = "Учетная запись не найдена";

    CourierSteps courierSteps = new CourierSteps();

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://qa-scooter.education-services.ru";
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 5000)
                        .setParam("http.socket.timeout", 5000)
                        .setParam("http.connection-manager.timeout", 5000));

    }

    @AfterEach
    void tearDown() {
        if (courier != null) {
            courierSteps.loginAndDeleteCourier(courier);
        }
    }

    @Step("Сравниваю текст и код ответа")
    public void checkSuccessResponse(Response response) {

        response.then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Step("Сравниваю текст и код ответа")
    public void checkErrorResponse(Response response, String message, int code) {

        response.then()
                .statusCode(code)
                .body("message", equalTo(message));
    }

    @Test
    @DisplayName("Логин курьера в системе")
    public void loginCourierCorrectLoginAndPasswordSuccess() {

        courier = courierSteps.createCourierBeforeLogin();
        Response response = courierSteps.loginCourier(courier);
        checkSuccessResponse(response);

    }

    @Test
    @DisplayName("Логин курьера в системе: пароль не подходит")
    public void loginCourierIncorrectPasswordError() {

        login = courierSteps.createLogin();
        password = courierSteps.createPassword();
        courier = courierSteps.createCourier(login, password);
        String incorrectPassword = courierSteps.createPassword();
        Response responseError = courierSteps.loginCourier(login, incorrectPassword);
        checkErrorResponse(responseError, NOT_FOUND_COURIER, 404);


    }

    @Test
    @DisplayName("Логин курьера в системе: без логина")
    public void loginCourierLoginIsNullInsufficientDataError() {

        password = courierSteps.createPassword();
        Response responseError = courierSteps.loginCourier(null, password);
        checkErrorResponse(responseError, INSUFFICIENT_DATA_MESSAGE, 400);

    }

    @Test
    @DisplayName("Логин курьера в системе: без пароля")
    public void loginCourierPasswordIsNullInsufficientDataError() {

        login = courierSteps.createLogin();
        Response responseError = courierSteps.loginCourier(login, null);
        checkErrorResponse(responseError, INSUFFICIENT_DATA_MESSAGE, 400);

    }

    @Test
    @DisplayName("Логин курьера в системе: без логина и пароля")
    public void loginCourierLoginAndPasswordIsNullInsufficientDataError() {

        Response responseError = courierSteps.loginCourier(null, null);
        checkErrorResponse(responseError, INSUFFICIENT_DATA_MESSAGE, 400);

    }

    @Test
    @DisplayName("Логин курьера в системе: учётная запись не найдена")
    public void loginCourierIncorrectLoginError() {

        login = courierSteps.createLogin();
        password = courierSteps.createPassword();
        Response responseError = courierSteps.loginCourier(login, password);
        checkErrorResponse(responseError, NOT_FOUND_COURIER, 404);

    }
}
