package com.godcare.api.repository;

import com.godcare.api.entity.Product;
import com.godcare.api.vo.PageableRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class EmProductRepository {

    private final EntityManager em;

    // 최신순
    public List<Product> findProductsWithCursor(PageableRequest pageable) {
        return em.createQuery("SELECT p FROM Product p " +
                        "JOIN fetch p.category c " +
                        "WHERE p.id < :cursor " +
                        "AND p.isDeleted = false " +
                        "ORDER BY p.id DESC ", Product.class)
                .setParameter("cursor", pageable.getCursor())
                .setFirstResult(0)
                .setMaxResults(pageable.getSize())
                .getResultList();
    }

    public List<Product> findProducts(PageableRequest pageable) {
        return em.createQuery("SELECT p FROM Product p " +
                        "JOIN fetch p.category c " +
                        "WHERE p.isDeleted = false " +
                        "ORDER BY p.id DESC ", Product.class)
                .setFirstResult(0)
                .setMaxResults(pageable.getSize())
                .getResultList();
    }

    // 가격 낮은순
    public List<Product> findProductsByPriceAscWithCursor(PageableRequest pageable) {
        return em.createQuery("SELECT p FROM Product p " +
                        "JOIN fetch p.category c " +
                        "WHERE p.price > :cursor " +
                        "AND p.isDeleted = false " +
                        "ORDER BY p.price ASC ", Product.class)
                .setParameter("cursor", BigDecimal.valueOf(pageable.getCursor()))
                .setFirstResult(0)
                .setMaxResults(pageable.getSize())
                .getResultList();
    }

    public List<Product> findProductsByPriceAsc(PageableRequest pageable) {
        return em.createQuery("SELECT p FROM Product p " +
                        " JOIN fetch p.category c " +
                        " WHERE p.isDeleted = false " +
                        " ORDER BY p.price ASC ", Product.class)
                .setFirstResult(0)
                .setMaxResults(pageable.getSize())
                .getResultList();
    }

    // 가격 높은순
    public List<Product> findProductsByPriceDescWithCursor(PageableRequest pageable) {
        return em.createQuery("SELECT p FROM Product p " +
                        "JOIN fetch p.category c " +
                        "WHERE p.price < :cursor " +
                        "AND p.isDeleted = false " +
                        "ORDER BY p.price DESC ", Product.class)
                .setParameter("cursor", BigDecimal.valueOf(pageable.getCursor()))
                .setFirstResult(0)
                .setMaxResults(pageable.getSize())
                .getResultList();
    }

    public List<Product> findProductsByPriceDesc(PageableRequest pageable) {
        return em.createQuery("SELECT p FROM Product p " +
                        "JOIN fetch p.category c " +
                        "WHERE p.isDeleted = false " +
                        "ORDER BY p.price DESC ", Product.class)
                .setFirstResult(0)
                .setMaxResults(pageable.getSize())
                .getResultList();
    }
}
