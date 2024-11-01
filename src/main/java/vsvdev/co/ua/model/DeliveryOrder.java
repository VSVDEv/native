package vsvdev.co.ua.model;

public class DeliveryOrder {

    private String email;
    private String city;
    private String name;
    private String address;
    private String phone;
    private Integer quantity;
    private String deliveryDate;
    private String status;
    private String notes;

    public DeliveryOrder(String email, String city, String name, String address, String phone, Integer quantity, String deliveryDate, String status, String notes) {
        this.email = email;
        this.city = city;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.quantity = quantity;
        this.deliveryDate = deliveryDate;
        this.status = status;
        this.notes = notes;
    }

    public DeliveryOrder() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

   public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

   public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "{" +
                "\"email\": \"" + email + "\"," +
                "\"city\": \"" + city + "\"," +
                "\"name\": \"" + name + "\"," +
                "\"address\": \"" + address + "\"," +
                "\"phone\": \"" + phone + "\"," +
                "\"quantity\": " + quantity + "," +
                "\"deliveryDate\": \"" + deliveryDate + "\"," +
                "\"status\": \"" + status + "\"," +
                "\"notes\": \"" + notes + "\"" +
                "}";
    }

}
