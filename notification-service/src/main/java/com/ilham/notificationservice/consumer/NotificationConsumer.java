
package com.ilham.notificationservice.consumer;

import com.ilham.notificationservice.dto.OrderNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {

    private final JavaMailSender mailSender;

    @RabbitListener(queues = "order.notification")
    public void consume(OrderNotification notification) {

        log.info("Message received: {}", notification);

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(notification.getEmail());
        message.setSubject("Yeni sifariş");
        message.setText(
                "Order ID: " + notification.getOrderId() +
                        "\nProduct: " + notification.getProductName() +
                        "\nUser ID: " + notification.getUserId()
        );

        mailSender.send(message);

        log.info("Mail sent successfully");
    }
}
