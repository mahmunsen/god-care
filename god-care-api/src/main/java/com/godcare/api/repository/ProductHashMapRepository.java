package com.godcare.api.repository;

import com.godcare.api.entity.ProductV0;
import com.godcare.common.dto.ViewProductListResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


@Repository
public class ProductHashMapRepository {
    Map<Long, ProductV0> storage = new ConcurrentSkipListMap<>(Comparator.reverseOrder());
    private AtomicLong sequence = new AtomicLong(1L);

    public ProductV0 save(ProductV0 product) {
        product.assignId(sequence.getAndIncrement());
        storage.put(product.getId(), product);
        return product;
    }

    public ProductV0 findById(Long productId) {
        ProductV0 found = storage.get(productId);
        return found;
    }

    public List<ViewProductListResponse> findAll() {
        Collection<ProductV0> values = storage.values();
        return values.stream().map(product -> new ViewProductListResponse(product.getMainImg(), product.getCategoryId(), product.getId())).collect(Collectors.toList());
    }

    public void deleteById(Long productId) {
        storage.remove(productId);
    }

    public ProductV0 saveUpdated(ProductV0 product) {
        storage.put(product.getId(), product);
        return product;
    }

    public List<ProductV0> findAllByOrderByIdDesc(Pageable pageable) {
        return storage.values().stream().limit(pageable.getPageSize()).collect(Collectors.toList());
    }

    public List<ProductV0> findByIdGreaterThanOrderByIdDesc(Long id, Pageable pageable) {
        List<ProductV0> productList = new ArrayList<>();
        for (Long key : storage.keySet()) {
            if (id < key && key <= id + pageable.getPageSize()) {
                productList.add(storage.get(key));
            }
        }
        return productList;
    }

    public boolean existsByIdGreaterThan(Long lastId) {
        for (Long key : storage.keySet()) {
            if (key > lastId) {
                return true;
            }
        }
        return false;
    }
}
