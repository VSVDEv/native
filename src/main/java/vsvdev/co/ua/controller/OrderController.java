package vsvdev.co.ua.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import vsvdev.co.ua.model.DeliveryOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;


@RestController
@RequestMapping("/orders")
public class OrderController {

    public static final String TABLE_PARTITION_KEY = "email";
    public static final String GSI_INDEX = "email-city-index";
    public static final String GSI_INDEX_PARTITION_KEY = "city";
    public static final String TABLE = "DeliveryOrder";
    public static final Logger LOG = Logger.getLogger(OrderController.class.getName());

    private final DynamoDbClient client;


    public OrderController(){
        client = DynamoDbClient.builder().region(Region.US_EAST_1).build();
    }




    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<DeliveryOrder> addOrder(@RequestBody DeliveryOrder order) {

        putOrder(order);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }


    @GetMapping(produces = "application/json")
    public ResponseEntity<List<DeliveryOrder>> getOrders(@RequestParam(name = "city", required = false) String city) {
        List<DeliveryOrder> orders;
            orders = getOrdersList();
            return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping(value = "/{email}", produces = "application/json")
    public ResponseEntity<DeliveryOrder> getOrderByEmail(@PathVariable String email) {

        LOG.info("The email to find is: " + email);
        DeliveryOrder order = findOrder(email);
        LOG.info("The found order's email is: " + email);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PutMapping(value = "/{email}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> updateOrder(@PathVariable String email, @RequestBody DeliveryOrder order) {

        LOG.info("The email to update is: " + email);

        DeliveryOrder existingOrder = findOrder(email);

            updateDeliveryNotNullAttributes(existingOrder, order);
            putOrder(existingOrder);
        LOG.info("The updated order's email is: " + existingOrder.getEmail());
            return new ResponseEntity<>(order.toString(), HttpStatus.OK);
    }


    @DeleteMapping(value = "/{email}", produces = "application/json")
    public ResponseEntity<String> deleteCourse(@PathVariable String email) {
        LOG.info("The email to delete is: " + email);
        String deleted = deleteOrder(email);
        if (deleted.equals("not found")) {
            return new ResponseEntity<>(deleted, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(deleted, HttpStatus.NO_CONTENT);
        }
    }

    private String deleteOrder(String email) {
        Map<String, AttributeValue> map = new HashMap<>();
        map.put(TABLE_PARTITION_KEY, AttributeValue.builder().s(email).build());

        LOG.info("deleting email: " + email);
        DeleteItemResponse deleteItemResponse = client.deleteItem(DeleteItemRequest.builder()
                .tableName(TABLE)
                .key(map)
                .build());
        LOG.info("deleting status: " + deleteItemResponse.sdkHttpResponse().statusCode());
        if (deleteItemResponse.sdkHttpResponse().isSuccessful()) {
            return "Deleted";
        } else {
            return "not found";
        }
    }


    private DeliveryOrder findOrder(String email) {

        Map<String, AttributeValue> map = new HashMap<>();
        map.put(TABLE_PARTITION_KEY, AttributeValue.builder().s(email).build());

        LOG.info("searching: " + email);
        GetItemRequest request = GetItemRequest.builder()
                .tableName(TABLE)
                .key(map)
                .consistentRead(false)
                .build();

        Map<String, AttributeValue> item = client.getItem(request).item();


        LOG.info("found keys: " + item.keySet().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ")));

        LOG.info("found values: " + item.values().stream()
                .map(AttributeValue::toString)
                .collect(Collectors.joining(", ")));
        return toDeliveryOrder(item);
    }


    private void putOrder(DeliveryOrder order) {

        LOG.info("putting order: " + order.getEmail());
        Map<String, AttributeValue> map = toMap(order);
        PutItemRequest itemRequest = PutItemRequest.builder()
                .tableName(TABLE)
                .item(map)
                .build();
        client.putItem(itemRequest);
    }

    public List<DeliveryOrder> getOrdersList() {
        ScanRequest scanRequest2 = ScanRequest.builder()
                .tableName(TABLE)
                .build();
        ScanResponse scan = client.scan(scanRequest2);
        return scan.items().stream().map(this::toDeliveryOrder).toList();
    }

    private Map<String, AttributeValue> toMap(DeliveryOrder order) {

        Map<String, AttributeValue> map = new HashMap<>();
        map.put("email", AttributeValue.fromS(order.getEmail()));
        map.put("city", AttributeValue.fromS(order.getCity()));
        map.put("name", AttributeValue.fromS(order.getName()));
        map.put("address", AttributeValue.fromS(order.getAddress()));
        map.put("phone", AttributeValue.fromS(order.getPhone()));
        map.put("quantity", AttributeValue.fromS(String.valueOf(order.getQuantity())));
        map.put("deliveryDate", AttributeValue.fromS(order.getDeliveryDate()));
        map.put("status", AttributeValue.fromS(order.getStatus()));
        map.put("notes", AttributeValue.fromS(order.getNotes()));

        return map;
    }

    private DeliveryOrder toDeliveryOrder(Map<String, AttributeValue> item) {
        DeliveryOrder order = new DeliveryOrder();

        if (item.containsKey("email")) {
            order.setEmail(item.get("email").s());
        }
        if (item.containsKey("city")) {
            order.setCity(item.get("city").s());
        }
        if (item.containsKey("address")) {
            order.setAddress(item.get("address").s());
        }
        if (item.containsKey("name")) {
            order.setName(item.get("name").s());
        }
        if (item.containsKey("phone")) {
            order.setPhone(item.get("phone").s());
        }
        if (item.containsKey("deliveryDate")) {
            order.setDeliveryDate(item.get("deliveryDate").s());
        }
        if (item.containsKey("quantity")) {
            order.setQuantity(parseInt(item.get("quantity").s()));
        }
        if (item.containsKey("status")) {
            order.setStatus(item.get("status").s());
        }
        if (item.containsKey("notes")) {
            order.setNotes(item.get("notes").s());
        }
        return order;
    }


    private static void updateDeliveryNotNullAttributes(DeliveryOrder existingOrder, DeliveryOrder inputOrder) {
        if (inputOrder.getAddress() != null) {
            existingOrder.setAddress(inputOrder.getAddress());
        }
        if (inputOrder.getName() != null) {
            existingOrder.setName(inputOrder.getName());
        }
        if (inputOrder.getCity() != null) {
            existingOrder.setCity(inputOrder.getCity());
        }
        if (inputOrder.getPhone() != null) {
            existingOrder.setPhone(inputOrder.getPhone());
        }
        if (inputOrder.getDeliveryDate() != null) {
            existingOrder.setDeliveryDate(inputOrder.getDeliveryDate());
        }
        if (inputOrder.getQuantity() != null) {
            existingOrder.setQuantity(inputOrder.getQuantity());
        }
        if (inputOrder.getStatus() != null) {
            existingOrder.setStatus(inputOrder.getStatus());
        }
        if (inputOrder.getNotes() != null) {
            existingOrder.setNotes(inputOrder.getNotes());
        }
    }

}