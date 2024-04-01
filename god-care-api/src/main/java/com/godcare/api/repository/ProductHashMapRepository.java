package com.godcare.api.repository;

import com.godcare.api.entity.ProductV1;
import com.godcare.common.dto.ViewProductListResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


@Repository
public class ProductHashMapRepository {
    Map<Long, ProductV1> storage = new TreeMap<>();
    private AtomicLong sequence = new AtomicLong(1L);
    public ProductV1 save(ProductV1 product) {
        product.assignId(sequence.getAndIncrement());
        storage.put(product.getId(), product);
        return product;
    }
    public ProductV1 findById(Long productId) {
         ProductV1 found = storage.get(productId);
        return found;
    }
    public List<ViewProductListResponse> findAll() {
        Collection<ProductV1> values = storage.values();
        return values.stream().map(product -> new ViewProductListResponse(product.getMainImg(), product.getCategoryId(), product.getId())).collect(Collectors.toList());
    }
    public void deleteById(Long productId) {
        storage.remove(productId);
    }
    public ProductV1 saveUpdated(ProductV1 product) {
        storage.put(product.getId(), product);
        return product;
    }

    public List<ProductV1> findAllByOrderByIdDesc(Pageable pageable) {
        List<ProductV1> productList = new ArrayList<>();
        for(Long key: storage.keySet()){
                productList.add(storage.get(key));
        }
        Collections.reverse(productList);
        int end = productList.size();
        return productList.subList(end - pageable.getPageSize(), end);
    }
    public List<ProductV1> findByIdLessThanOrderByIdDesc(Long id, Pageable pageable) {
        List<ProductV1> productList = new ArrayList<>();
        for(Long key: storage.keySet()){
            if(key>id){
                productList.add(storage.get(key));
            }
        }
        Collections.reverse(productList);
        int end = productList.size();
        return productList.subList(end - pageable.getPageSize(), end);
    }

    public boolean existsByIdLessThan(Long lastId) {
        for (Long key : storage.keySet()) {
            if (key > lastId) {
                return true;
            }
        }
        return false;
    }
}
