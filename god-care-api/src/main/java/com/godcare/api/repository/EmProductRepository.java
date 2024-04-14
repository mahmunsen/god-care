package com.godcare.api.repository;

import com.godcare.api.entity.Product;
import com.godcare.api.util.DateUtils;
import com.godcare.api.vo.PageableRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class EmProductRepository {

    private final EntityManager em;

    // 최신순
    public List<Product> findProductsWithCursor(PageableRequest pageable) {
        String queryString = "SELECT p FROM Product p " +
                "JOIN fetch p.category c " +
                "WHERE p.timeUpdated < :cursor " +
                "AND p.isDeleted = false " +
                "ORDER BY p.timeUpdated DESC ";

        Query query = em.createQuery(queryString, Product.class);

        query.setParameter("cursor", DateUtils.convertToInstant(pageable.getCursor(), DateUtils.yearMonthDayHourMinuteSecond))
                .setFirstResult(0)
                .setMaxResults(pageable.getSize());

        return query.getResultList();
    }

    public List<Product> findProducts(PageableRequest pageable) {
        String queryString = "SELECT p FROM Product p " +
                "JOIN fetch p.category c " +
                "WHERE p.isDeleted = false " +
                "ORDER BY p.timeUpdated DESC ";

        Query query = em.createQuery(queryString, Product.class);

        query.setFirstResult(0)
                .setMaxResults(pageable.getSize());

        return query.getResultList();
    }

    // 가격 낮은순
    public List<Product> findProductsByPriceAscWithCursor(PageableRequest pageable) {
        String queryString = "SELECT p FROM Product p " +
                "JOIN fetch p.category c " +
                "WHERE p.price > :cursor " +
                "AND p.isDeleted = false " +
                "ORDER BY p.price ASC ";

        Query query = em.createQuery(queryString, Product.class);

        query.setParameter("cursor", BigDecimal.valueOf(Long.parseLong(pageable.getCursor())))
                .setFirstResult(0)
                .setMaxResults(pageable.getSize());

        return query.getResultList();
    }

    public List<Product> findProductsByPriceAsc(PageableRequest pageable) {
        String queryString = "SELECT p FROM Product p " +
                "JOIN fetch p.category c " +
                "WHERE p.isDeleted = false " +
                "ORDER BY p.price ASC ";

        Query query = em.createQuery(queryString, Product.class);

        query.setFirstResult(0)
                .setMaxResults(pageable.getSize());

        return query.getResultList();
    }

    // 가격 높은순
    public List<Product> findProductsByPriceDescWithCursor(PageableRequest pageable) {
        String queryString = "SELECT p FROM Product p " +
                "JOIN fetch p.category c " +
                "WHERE p.price < :cursor " +
                "AND p.isDeleted = false " +
                "ORDER BY p.price DESC ";

        Query query = em.createQuery(queryString, Product.class);

        query.setParameter("cursor", BigDecimal.valueOf(Long.valueOf(pageable.getCursor())))
                .setFirstResult(0)
                .setMaxResults(pageable.getSize());

        return query.getResultList();
    }

    public List<Product> findProductsByPriceDesc(PageableRequest pageable) {
        String queryString = "SELECT p FROM Product p " +
                "JOIN fetch p.category c " +
                "WHERE p.isDeleted = false " +
                "ORDER BY p.price DESC ";

        Query query = em.createQuery(queryString, Product.class);

        query.setFirstResult(0)
                .setMaxResults(pageable.getSize());

        return query.getResultList();
    }
}
