import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import steps.CourierSteps;
import steps.OrderSteps;
import java.util.List;
import java.util.stream.Stream;
import static org.hamcrest.Matchers.*;

@Execution(ExecutionMode.CONCURRENT)
@DisplayName("Функциональность: Получение списка заказов")
public class OrdersListTest {

    private Integer courierId;
    private static final List<String> STATIONS = List.of("3", "5");
    CourierSteps courierSteps = new CourierSteps();
    OrderSteps orderSteps = new OrderSteps();

    private static Stream<Arguments> requestsParameters() {
        return Stream.of(
                Arguments.of( "0, 30, [\"1\",\"2\"]", 0, 30, List.of("1","2")),
                Arguments.of("1, 29, [\"9\"]", 1, 29, List.of("9")),
                Arguments.of("null, null, null, null", null, null, null)
        );
    }

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://qa-scooter.education-services.ru";
    }


    @AfterEach
    void tearDown() {
        if (courierId != null) {
            courierSteps.deleteCourier(courierId);
        }
    }


    @Step("Сравниваю текст и код ответа на запрос без параметров")
    public void checkSuccessResponse(Response response) {
        response.then()
                .statusCode(200)
                .body("pageInfo.page", equalTo(0),
                        "pageInfo.limit", equalTo(30),
                        "orders", hasSize(30));
    }

    @Step("Сравниваю текст и код ответа")
    public void checkErrorResponse(Response response, String message, int code) {
        response.then()
                .statusCode(code)
                .body("message", equalTo(message));
    }

    @Step("Сравниваю текст и код ответа на запрос с параметрами")
    public void checkSuccessResponse(Response response, Integer page, Integer limit, List<String> nearestStation) {
        if(page == null && limit == null) {
            checkSuccessResponse(response);
        } else {
            response.then()
                    .statusCode(200)
                    .body("orders", not(empty()))
                    .body("pageInfo.page", equalTo(page),
                            "pageInfo.limit", equalTo(limit),
                            "orders.metroStation", everyItem(isIn(nearestStation)));
        }

    }

    @Step("Сравниваю текст и код ответа на запрос с параметром курьера")
    public void checkSuccessResponse(Response response, Integer courierId, List<String> nearestStation) {
        response.then()
                .statusCode(200)
                .body("orders.courierId", everyItem(equalTo(courierId)),
                    "orders.metroStation", everyItem(isIn(nearestStation)));


    }

    @Test
    @DisplayName("Запрос списка заказов без параметров")
    public void getOrdersDefault() {
        Response response = orderSteps.getOrders();
        checkSuccessResponse(response);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("requestsParameters")
    @DisplayName("Запрос списка заказов c параметрами:")
    public void getOrdersByParamsSuccess(String testName, Integer page, Integer limit, List<String> nearestStation) {
        Response response = orderSteps.getOrdersByParams(page, limit, nearestStation);
        checkSuccessResponse(response, page, limit, nearestStation);
    }


    @Test
    @DisplayName("Запрос списка заказов курьера: курьер не найден")
    public void getOrderByUnknownCourierError() {
        Response response = orderSteps.getOrdersByCourier(1);
        checkErrorResponse(response, "Курьер с идентификатором 1 не найден", 404);
    }

    @Test
    @DisplayName("Запрос списка заказов курьера")
    public void getOrdersByCourierSuccess() {

        courierId = courierSteps.createAndLoginCourier();
        orderSteps.assignOrder(STATIONS.get(0), courierId);
        orderSteps.assignOrder(STATIONS.get(1), courierId);
        Response response = orderSteps.getOrdersByCourier(courierId);
        checkSuccessResponse(response, courierId, STATIONS);


    }


}
