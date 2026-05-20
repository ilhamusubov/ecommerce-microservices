package com.ilham.orderservice.dao.response;

import lombok.*;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderNotification implements Serializable {

    private Long orderId;

    private Long userId;

    private String productName;

    private String email;
}
