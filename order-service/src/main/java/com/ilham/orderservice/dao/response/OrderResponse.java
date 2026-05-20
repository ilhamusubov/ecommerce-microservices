package com.ilham.orderservice.dao.response;

import com.ilham.orderservice.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderResponse {
    private Long id;
    private Long userId;
    private String name;
    private BigDecimal totalPrice;
    private OrderStatus status;
}
