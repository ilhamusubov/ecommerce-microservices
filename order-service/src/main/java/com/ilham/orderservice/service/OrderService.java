package com.ilham.orderservice.service;

import com.ilham.orderservice.client.ProductClient;
import com.ilham.orderservice.dao.request.OrderRequest;
import com.ilham.orderservice.dao.response.OrderNotification;
import com.ilham.orderservice.dao.response.OrderResponse;
import com.ilham.orderservice.dao.response.ProductResponse;
import com.ilham.orderservice.entity.OrderEntity;
import com.ilham.orderservice.enums.OrderStatus;
import com.ilham.orderservice.event.OrderCreatedEvent;
import com.ilham.orderservice.jwt.JwtService;
import com.ilham.orderservice.mapper.OrderMapper;
import com.ilham.orderservice.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final JwtService jwtService;
    private final ProductClient productClient;
    private final RabbitTemplate rabbitTemplate;

    public OrderResponse createOrder(OrderRequest orderRequest, HttpServletRequest request){
        log.info("ActionLog.createOrder.starts");

        Long userId = jwtService.extractUserIdFromAccessToken(request);

        String email = jwtService.extractEmailFromAccessToken(request);

        ProductResponse product = productClient.getProduct(orderRequest.getProductId());

        if(product.getStock() < orderRequest.getQuantity()) {
            throw new RuntimeException("Not enough stock");
        }

        OrderEntity orderEntity = OrderEntity.builder()
                .userId(userId)
                .name(product.getName())
                .status(OrderStatus.PENDING)
                .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(orderRequest.getQuantity())))
                .build();

        orderRepository.save(orderEntity);

        OrderNotification notification = OrderNotification.builder()
                .orderId(orderEntity.getId())
                .userId(userId)
                .productName(product.getName())
                .email(email)
                .build();

        rabbitTemplate.convertAndSend("order.notification", notification);

        OrderCreatedEvent orderCreatedEvent = OrderCreatedEvent.builder()
                .productId(product.getId())
                .quantity(orderRequest.getQuantity())
                .build();

        rabbitTemplate.convertAndSend("product.stock", orderCreatedEvent);

        log.info("ActionLog.createOrder.finished");
        return orderMapper.orderToOrderResponse(orderEntity);
    }
}
