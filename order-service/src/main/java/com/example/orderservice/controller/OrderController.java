package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDTO;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.messageQueue.KafkaProducer;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import com.example.orderservice.messageQueue.OrderProducer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order-service")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final Environment env;
    private final KafkaProducer kafkaProducer;
    private final OrderProducer orderProducer;

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in Order Service on Port %s",
            env.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable String userId, @RequestBody RequestOrder req){
        OrderDTO orderDTO = new ModelMapper().map(req, OrderDTO.class);
        orderDTO.setUserId(userId);
        orderDTO.setOrderId(UUID.randomUUID().toString());
        orderDTO.setTotalPrice(orderDTO.getQty() * orderDTO.getUnitPrice());
        //OrderDTO order = orderService.createOrder(orderDTO);
        ResponseOrder responseOrder = new ModelMapper().map(orderDTO, ResponseOrder.class);

        kafkaProducer.send("example-catalog-topic", orderDTO);
        orderProducer.send("orders", orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrders(@PathVariable String userId){
        Iterable<OrderEntity> orders = orderService.getOrdersByUserId(userId);
        List<ResponseOrder> results = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        orders.forEach(v -> {
            results.add(modelMapper.map(v, ResponseOrder.class));
        });
        return ResponseEntity.status(HttpStatus.OK).body(results);
    }
}
