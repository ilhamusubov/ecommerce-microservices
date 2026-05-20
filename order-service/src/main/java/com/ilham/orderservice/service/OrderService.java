package com.ilham.orderservice.service;

import com.ilham.orderservice.dao.request.OrderRequest;
import com.ilham.orderservice.dao.response.OrderResponse;
import com.ilham.orderservice.entity.OrderEntity;
import com.ilham.orderservice.enums.OrderStatus;
import com.ilham.orderservice.jwt.JwtService;
import com.ilham.orderservice.mapper.OrderMapper;
import com.ilham.orderservice.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Order;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final JwtService jwtService;

    public OrderResponse createOrder(OrderRequest orderRequest, HttpServletRequest request){
        log.info("ActionLog.createOrder.starts");

        Long userId = jwtService.extractUserIdFromAccessToken(request);

        OrderEntity orderEntity = OrderEntity.builder()
                .userId(userId)
                .name(orderRequest.getName())
                .status(OrderStatus.PENDING)
                .totalPrice(orderRequest.getTotalPrice())
                .build();

        orderRepository.save(orderEntity);

        log.info("ActionLog.createOrder.finished");
        return orderMapper.orderToOrderResponse(orderEntity);
    }
}
