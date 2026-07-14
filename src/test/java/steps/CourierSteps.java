package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import pojo.Courier;
import java.util.UUID;
import static io.restassured.RestAssured.given;


public class CourierSteps {

    @Step("Создаю уникальный логин")
    public String createLogin() {
        return "Moshka" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Step("Создаю уникальный пароль")
    public String createPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Step("Создаю курьера")
    public Response createCourier(Courier courier) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(courier)
                        .when()
                        .post("/api/v1/courier");
        return response;
    }

    @Step("Создаю курьера")
    public Courier createCourier(String login, String password) {
        Courier courier = new Courier(login,password);
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(courier)
                        .when()
                        .post("/api/v1/courier");
        return courier;
    }

    @Step("Создаю курьера для проверки логина")
    public Courier createCourierBeforeLogin(){
        String login = createLogin();
        String password = createPassword();
        Courier courier = new Courier(login, password);
        createCourier(courier);
        return courier;
    }


    @Step("Удаляю созданного курьера")
    public void deleteCourier(int id) {
        given()
                .header("Content-type", "application/json")
                .when()
                .delete("/api/v1/courier/" + id);

    }

    @Step("Выполняю логин и удаляю курьера по id")
    public void loginAndDeleteCourier(Courier courier){
        Response response = loginCourier(courier);
        int courierId = response.path("id");
        deleteCourier(courierId);

    }

    @Step("Выполняю логин курьера")
    public Response loginCourier (String login, String password) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(new Courier(login, password))
                        .when()
                        .post("/api/v1/courier/login");

        return response;

    }

    @Step("Выполняю логин курьера")
    public Response loginCourier (Courier courier) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(courier)
                        .when()
                        .post("/api/v1/courier/login");

        return response;

    }

    @Step("Создаю и выполняю логин курьера")
    public int createAndLoginCourier() {
        String login = createLogin();
        String password = createPassword();
        Courier courier = new Courier(login,password,null);
        createCourier(courier);
        Response response = loginCourier(courier);
        int id = response.jsonPath().getInt("id");
        return id;
    }

}
