package com.ilham.orderservice.mapper;

import com.ilham.orderservice.dao.request.OrderRequest;
import com.ilham.orderservice.dao.response.OrderResponse;
import com.ilham.orderservice.entity.OrderEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse orderToOrderResponse(OrderEntity order);
}
