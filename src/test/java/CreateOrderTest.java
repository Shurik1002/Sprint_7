import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pojo.Order;
import steps.OrderSteps;
import java.util.stream.Stream;
import static org.hamcrest.core.IsNull.notNullValue;

@Execution(ExecutionMode.CONCURRENT)
@DisplayName("Функциональность: Создание заказа")
public class CreateOrderTest {

    OrderSteps orderSteps = new OrderSteps();

    private static Stream<Arguments> orderProvider() {
        return Stream.of(
                Arguments.of( "указан чёрный цвет, отсутвуют имя и фамилия", null, null, "Москва", "2", "89000000000", 5, "2026-06-08", "коммент", new String[]{"BLACK"}),
                Arguments.of("указан серый цвет, отсутвуют адрес и станция", "Тест", "Тестовый", null, null, "89000000000", 5, "2026-06-08", "коммент", new String[]{"GREY"}),
                Arguments.of("указаны оба цвета, отсутсвуют телефон и срок аренды", "Тест", "Тестовый", "Москва", "2", null, null, "2026-06-08", "коммент", new String[]{"BLACK", "GREY"}),
                Arguments.of("цвет не указан, отсутсвуют дата и комментарий", "Тест", "Тестовый", "Москва", "2", "89000000000", 5, null, null, new String[]{}),
                Arguments.of("полный набор данных", "Тест", "Тестовый", "Москва", "2", "89000000000", 5, "2026-06-08", "коммент", new String[]{"BLACK", "GREY"})
        );
    }

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://qa-scooter.education-services.ru";
    }

    @Step("Сравниваю текст и код ответа")
    public void checkSuccessResponse(Response response) {
        response.then()
                .statusCode(201)
                .body("track", notNullValue());
    }

    @ParameterizedTest(name = "{0}")
    @DisplayName("Создание заказа:")
    @MethodSource("orderProvider")
    public void createOrder(String testName, String firstName, String lastName, String address, String metroStation, String phone, Integer rentTime, String deliveryDate, String comment, String[] color) {

        Order order = new Order(
                firstName,
                lastName,
                address,
                metroStation,
                phone,
                rentTime,
                deliveryDate,
                comment,
                color
        );

        Response response = orderSteps.orderCreate(order);
        checkSuccessResponse(response);


    }



}
