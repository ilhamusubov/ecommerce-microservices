package com.ilham.orderservice.client;

import com.ilham.orderservice.config.FeignConfig;
import com.ilham.orderservice.dao.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PRODUCT-SERVICE", configuration = FeignConfig.class)
public interface ProductClient {

    @GetMapping("/api/v1/products/get-product/{id}")
    ProductResponse getProduct(@PathVariable Long id);
}
