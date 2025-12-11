package com.example.restaurantorderservice.http;

import com.example.restaurantorderservice.domain.model.Order;
import com.example.restaurantorderservice.domain.service.OrderService;
import com.example.restaurantorderservice.http.dto.request.OrderRequestDto;
import com.example.restaurantorderservice.http.dto.response.OrderIdResponseDto;
import com.example.restaurantorderservice.http.dto.response.OrderResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@CrossOrigin(
    origins = "http://localhost:3000",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE}
)
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        int j = 0;
        for (int i = 0; i < 100000; i++) {
            j += 1;
        }
        return ResponseEntity.ok().body(Integer.toString(j));
    }

    @Operation(
        summary = "Creating/placing order",
        description = "Should respond with OrderId"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "201",
                description = "Created"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Bad Request"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            ),
        }
    )
    @PostMapping("/orders")
    public ResponseEntity<OrderIdResponseDto> createOrder(
        @RequestBody @Valid OrderRequestDto req) {
        Order order = orderService.createOrder(req);
        OrderIdResponseDto response = new OrderIdResponseDto(order.getOrderId(), order.getTotalPrice());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Get order by ID",
        description = "Should respond with a specific order"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Successful"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Order Not Found"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            ),
        }
    )
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(
        @PathVariable UUID orderId) {
        Order order = orderService.getOrder(orderId);
        return ResponseEntity.ok(OrderResponseDto.fromOrder(order));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDto>> getAllOrder() {
        List<OrderResponseDto> orders = orderService.getAllOrders()
            .stream()
            .map(OrderResponseDto::fromOrder)
            .toList();
        return ResponseEntity.ok(orders);
    }

    @Operation(
        summary = "Remove order by ID",
        description = "Should respond with a specific order"
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "204",
                description = "No Content - Successfully deleted Order"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Order Not Found"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            ),
        }
    )
    @DeleteMapping("/orders/cancel/{orderId}")
    public ResponseEntity<Void> cancelOrder(
        @PathVariable UUID orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }


}


