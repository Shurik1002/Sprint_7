import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import pojo.Courier;
import steps.CourierSteps;
import static org.hamcrest.Matchers.equalTo;

@Execution(ExecutionMode.CONCURRENT)
@DisplayName("Функциональность: Создание курьера")
public class CreateCourierTest {

    private static final String INSUFFICIENT_DATA_MESSAGE = "Недостаточно данных для создания учетной записи";
    private static final String EXIST_LOGIN_MESSAGE = "Этот логин уже используется. Попробуйте другой.";

    CourierSteps courierSteps = new CourierSteps();

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://qa-scooter.education-services.ru";
    }


    @Step("Сравниваю текст и код ответа")
    public void checkSuccessResponse(Response response) {

        response.then()
                .statusCode(201)
                .body("ok", equalTo(true));

    }

    @Step("Сравниваю текст и код ответа")
    public void checkErrorResponse(Response response, String message, int code) {

        response.then()
                .statusCode(code)
                .body("message", equalTo(message));

    }

    @Test
    @DisplayName("Создание курьера: с именем")
    public void createCourierWithAllFieldSuccess() {

        String login = courierSteps.createLogin();
        String password = courierSteps.createPassword();
        Response response = courierSteps.createCourier(new Courier(login, password, "Test"));
        checkSuccessResponse(response);

    }

    @Test
    @DisplayName("Создание курьера: без имени")
    public void createCourierFirstNameIsNullSuccess() {

        String login = courierSteps.createLogin();
        String password = courierSteps.createPassword();
        Response response = courierSteps.createCourier(new Courier(login, password, null));
        checkSuccessResponse(response);

    }

    @Test
    @DisplayName("Содание курьера: без логина")
    public void createCourierLoginIsNullInsufficientDataError() {

        String password = courierSteps.createPassword();
        Response response = courierSteps.createCourier(new Courier(null, password, "Test"));
        checkErrorResponse(response, INSUFFICIENT_DATA_MESSAGE, 400);

    }

    @Test
    @DisplayName("Создание курьера: без пароля")
    public void createCourierPasswordIsNullInsufficientDataError() {

        String login = courierSteps.createLogin();
        Response response = courierSteps.createCourier(new Courier(login, null, "Test"));
        checkErrorResponse(response, INSUFFICIENT_DATA_MESSAGE, 400);

    }

    @Test
    @DisplayName("Создание курьера: без логина и пароля")
    public void createCourierLoginAndPasswordIsNullInsufficientDataError() {

        Response response = courierSteps.createCourier(new Courier(null, null, "Test"));
        checkErrorResponse(response, INSUFFICIENT_DATA_MESSAGE, 400);

    }

    @Test
    @DisplayName("Создание курьера: логин занят")
    public void createCourierWithExistingLoginError() {

        String login = courierSteps.createLogin();
        String password = courierSteps.createPassword();
        Response createResponse = courierSteps.createCourier(new Courier(login, password, "Test"));
        checkSuccessResponse(createResponse);
        Response response = courierSteps.createCourier(new Courier(login, password, "Test"));
        checkErrorResponse(response, EXIST_LOGIN_MESSAGE, 409);

    }


}
