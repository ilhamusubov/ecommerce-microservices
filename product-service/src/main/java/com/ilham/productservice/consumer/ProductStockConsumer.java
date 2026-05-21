package com.ilham.productservice.consumer;


import com.ilham.productservice.entity.ProductEntity;
import com.ilham.productservice.event.OrderCreatedEvent;
import com.ilham.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductStockConsumer {

    private final ProductRepository productRepository;

    @RabbitListener(queues = "product.stock")
    public void consumeQuantity(OrderCreatedEvent orderCreatedEvent) {
        log.info("Message received: {}", orderCreatedEvent);

        ProductEntity productEntity = productRepository.findById(orderCreatedEvent.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Integer stock = productEntity.getStock();
        if(stock < orderCreatedEvent.getQuantity()) {
            throw new RuntimeException("Not enough stock");
        }
        productEntity.setStock(stock - orderCreatedEvent.getQuantity());
        productRepository.save(productEntity);
        log.info("Product stock updated. New stock: {}", productEntity.getStock());
    }
}
