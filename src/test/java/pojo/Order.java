package pojo;

public class Order {

    private String firstName;
    private String lastName;
    private String address;
    private String metroStation;
    private  String phone;
    private Integer rentTime;
    private String deliveryDate;
    private String comment;
    private String[] color;

    public Order(String firstName, String lastName, String address, String metroStation, String phone, Integer rentTime, String deliveryDate, String comment, String[] color) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.rentTime = rentTime;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
        this.color = color;
    }

    public Order(){}

    public Order(String metroStation) {
        this.metroStation = metroStation;
    }


}
