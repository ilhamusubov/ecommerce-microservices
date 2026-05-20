package com.ilham.productservice.mapper;

import com.ilham.productservice.dao.request.ProductRequest;
import com.ilham.productservice.dao.response.ProductResponse;
import com.ilham.productservice.entity.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductEntity requestToEntity(ProductRequest productRequest);
    ProductResponse entityToResponse(ProductEntity productEntity);
}
