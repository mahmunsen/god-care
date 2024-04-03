package com.godcare.api.service;

import com.godcare.api.entity.ProductV0;
import com.godcare.api.repository.ProductHashMapRepository;
import com.godcare.api.vo.PageResult;
import com.godcare.common.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class ProductServiceV0 {

    private final ProductHashMapRepository productHashMapRepository;

    // 상품등록
    public ProductV0 addProduct(ResisterProductRequest request) {
        ProductV0 savedProduct = productHashMapRepository.save(ProductV0.from(request));
        return savedProduct;
    }

    // 상품조회
    public ViewProductResponse getProduct(Long productId) {
        ProductV0 product = productHashMapRepository.findById(productId);
        return new ViewProductResponse(product.getId(), product.getMainImg(), product.getCategoryId());
    }

    // 상품전체조회
    public List<ViewProductListResponse> getAllProducts() {
        return productHashMapRepository.findAll();
    }

    // 상품수정
    public ProductV0 updateProduct(Long productId, UpdateProductRequest updateProductRequest) {
        // 상품 찾기
        ProductV0 product = productHashMapRepository.findById(productId);
        // 해당 엔티티의 내용을 수정 사항과 맵핑하여 저장
        product.update(updateProductRequest.getMainImg(), updateProductRequest.getCategoryId());
        ProductV0 updatedProduct = productHashMapRepository.saveUpdated(product);
        return updatedProduct;
    }

    // 상품삭제
    public void deleteProduct(Long productId) {
        productHashMapRepository.deleteById(productId);
    }

    // 간략 페이징
    public PageResult<ViewProductListResponse> get(Long cursorId, Pageable page) {
        final List<ProductV0> products = getProducts(cursorId, page);
        final Long lastIdOfList = products.isEmpty() ?
                null : products.get(0).getId();

        List<ViewProductListResponse> list = products.stream().map((product) -> new ViewProductListResponse(product.getMainImg(), product.getCategoryId(), product.getId())).collect(Collectors.toList());
        return new PageResult<>(list, hasNext(lastIdOfList));
    }

    public List<ProductV0> getProducts(Long cursorId, Pageable page) {
        return cursorId == null ?
                productHashMapRepository.findAllByOrderByIdDesc(page) :
                productHashMapRepository.findByIdGreaterThanOrderByIdDesc(cursorId, page);
    }

    private Boolean hasNext(Long lastId) {
        if (lastId == null) return false;
        return productHashMapRepository.existsByIdGreaterThan(lastId);
    }
}
