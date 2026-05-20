package com.ilham.orderservice.controller;

import com.ilham.orderservice.dao.request.OrderRequest;
import com.ilham.orderservice.dao.response.OrderResponse;
import com.ilham.orderservice.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create-order")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest, HttpServletRequest request) {
        return ResponseEntity.ok(
                orderService.createOrder(orderRequest, request)
        );
    }
}
