package steps;

import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import pojo.Order;
import java.util.List;
import static io.restassured.RestAssured.given;

public class OrderSteps {

    private static final Gson GSON = new Gson();

    @Step("Создаю заказ")
    public Response orderCreate(Order order){
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(order)
                        .when()
                        .post("/api/v1/orders");
        return response;
    }

    @Step("Получаю заказ по полю track")
    public Response getOrderByTrack(int orderTrack){
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .queryParam("t", orderTrack)
                        .when()
                        .get("/api/v1/orders/track");
        return response;
    }

    @Step("Принимаю заказ")
    public void acceptOrder(int orderId, int courierId) {
        given()
                .header("Content-type", "application/json")
                .queryParam("courierId", courierId)
                .when()
                .put("/api/v1/orders/accept/" + orderId);
    }

    @Step("Получаю список заказов по запросу без параметров")
    public Response getOrders(){

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .when()
                        .get("/api/v1/orders");

        return response;
    }

    @Step("Получаю список заказов по запросу с параметрами")
    public Response getOrdersByParams(Integer page, Integer limit, List<String> nearestStation){
        String nearestStationJson = GSON.toJson(nearestStation);
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .queryParam("page", page)
                        .queryParam("limit", limit)
                        .queryParam("nearestStation", nearestStationJson)
                        .when()
                        .get("/api/v1/orders");

        return response;
    }

    @Step("Получаю список заказов курьера")
    public Response getOrdersByCourier(Integer courierId){

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .queryParam("courierId", courierId)
                        .when()
                        .get("/api/v1/orders");

        return response;
    }

    @Step("Получаю id заказа")
    public int getOrderId(Order order) {
        Response orderCreateResponse = orderCreate(order);
        int orderTrack = orderCreateResponse.jsonPath().getInt("track");
        Response getOrderResponse = getOrderByTrack(orderTrack);
        int orderId = getOrderResponse.jsonPath().getInt("order.id");
        return orderId;

    }

    @Step("Назначаю заказ курьеру")
    public void assignOrder(String metroStation, int courierId) {
        Order order = new Order(metroStation);
        int orderId = getOrderId(order);
        acceptOrder(orderId,courierId);
    }



}
