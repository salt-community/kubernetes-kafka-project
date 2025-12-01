import org.example.restaurantpaymentservice.dto.OrderEvent;

import org.example.restaurantpaymentservice.dto.OrderEvent.OrderItem;
import org.example.restaurantpaymentservice.enums.OrderStatus;
import org.example.restaurantpaymentservice.enums.PaymentStatus;
import org.example.restaurantpaymentservice.model.Payment;
import org.example.restaurantpaymentservice.service.PaymentService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
public class OrderCancelConsumerTests {
    private final PaymentService paymentService;

    static Random random = new Random();

    public  OrderEvent placedOrder;
    public  OrderEvent canceledOrder;
    public  OrderEvent CompletedOrder;

    public OrderCancelConsumerTests(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


    @BeforeAll
    static void setupPlacedOrder(){
        List<OrderItem> itemList = new ArrayList<>();

        itemList.add(correctOrderItem0());
        itemList.add(correctOrderItem1());

        OrderEvent.builder()
                .eventId(UUID.randomUUID())
                .orderId(UUID.randomUUID())
                .orderStatus(OrderStatus.PLACED)
                .createdAt(Instant.now().minus(1, ChronoUnit.MINUTES))
                .totalPrice(23.423)
                .items(itemList)
                .build();

    }

    @Test
    void test(){
        paymentService.refund(placedOrder.orderId(), BigDecimal.valueOf(placedOrder.totalPrice()));
        Payment updatedPayment = paymentService.getById(placedOrder.orderId());
        Assert.isTrue(updatedPayment.getStatus() == PaymentStatus.REFUNDED,"Pending status should be Refunded but is not");
    }



    public static OrderItem correctOrderItem0(){
      return  OrderItem.builder()
            .id(UUID.randomUUID())
            .itemId(0)
            .quantity(random.nextInt(5))
            .build();
    }
    public static OrderItem correctOrderItem1(){
        return  OrderItem.builder()
                .id(UUID.randomUUID())
                .itemId(1)
                .quantity(random.nextInt(5))
                .build();
    }
}





