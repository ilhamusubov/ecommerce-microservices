package com.ilham.productservice.service;

import com.ilham.productservice.dao.request.ProductRequest;
import com.ilham.productservice.dao.response.ProductResponse;
import com.ilham.productservice.entity.ProductEntity;
import com.ilham.productservice.mapper.ProductMapper;
import com.ilham.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductResponse createProduct(ProductRequest productRequest) {
        log.info("ActionLog.createProduct.start");
        ProductEntity productEntity = productMapper.requestToEntity(productRequest);
        productRepository.save(productEntity);
        log.info("ActionLog.createProduct.end");
        return productMapper.entityToResponse(productEntity);
    }


    public ProductResponse getProduct(Long id) {
        log.info("ActionLog.getProduct.start");
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product tapilmadi"));
        log.info("ActionLog.getProduct.end");
        return productMapper.entityToResponse(productEntity);
    }
}
