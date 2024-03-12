package com.godcare.api.repository;

import com.godcare.api.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ProductRepository extends HashMap<Long, Product>{
    private Long sequence = 0L;
    public Product save(Product product) {
        product.assignId(++sequence);
        put(product.getId(), product);
        return product;
    }
    public Product findById(Long productId) {
         Product found = get(productId);
        return found;
    }
    public void deleteById(Long productId) {
        remove(productId);
    }

    public Product saveUpdated(Product product) {
        put(product.getId(), product);
        return product;
    }
}
