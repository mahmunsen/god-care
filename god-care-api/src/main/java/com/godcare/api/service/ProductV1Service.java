package com.godcare.api.service;

import com.godcare.api.entity.ProductV1;
import com.godcare.api.repository.ProductHashMapRepository;
import com.godcare.api.vo.PageResponse;
import com.godcare.common.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class ProductV1Service {

    private final ProductHashMapRepository productHashMapRepository;

    // 상품등록
    public ProductV1 addProduct(ResisterProductRequest request) {
        ProductV1 savedProduct = productHashMapRepository.save(ProductV1.from(request));
        return savedProduct;
    }
    // 상품조회
    public ViewProductResponse getProduct(Long productId) {
        ProductV1 product = productHashMapRepository.findById(productId);
        return new ViewProductResponse(product.getId(), product.getMainImg(), product.getCategoryId());
    }
    // 상품전체조회
    public List<ViewProductListResponse> getAllProducts() {
        return productHashMapRepository.findAll();
    }
    // 상품수정
    public ProductV1 updateProduct(Long productId, UpdateProductRequest updateProductRequest) {
        // 상품 찾기
        ProductV1 product = productHashMapRepository.findById(productId);
        // 해당 엔티티의 내용을 수정 사항과 맵핑하여 저장
        product.update(updateProductRequest.getMainImg(), updateProductRequest.getCategoryId());
        ProductV1 updatedProduct = productHashMapRepository.saveUpdated(product);
        return updatedProduct;
    }

    // 상품삭제
    public void deleteProduct(Long productId) {
        productHashMapRepository.deleteById(productId);
    }

    public PageResponse<ViewProductListResponse> get(Long cursorId, Pageable page) {
        final List<ProductV1> products = getProducts(cursorId, page);
        final Long lastIdOfList = products.isEmpty() ?
                null : products.get(0).getId();

        List<ViewProductListResponse> list = products.stream().map((product) -> new ViewProductListResponse(product.getMainImg(), product.getCategoryId(), product.getId())).collect(Collectors.toList());
        return new PageResponse<>(list, hasNext(lastIdOfList));
    }

    public List<ProductV1> getProducts(Long cursorId, Pageable page) {
        return cursorId == null ?
                productHashMapRepository.findAllByOrderByIdDesc(page) :
                productHashMapRepository.findByIdLessThanOrderByIdDesc(cursorId, page); // id보다 작은 모든 엔티티를 id값 기준으로 내림차순 정렬하여 반환
    }

    private Boolean hasNext(Long lastId) {
        if (lastId == null) return false;
        return productHashMapRepository.existsByIdLessThan(lastId);
    }
}
