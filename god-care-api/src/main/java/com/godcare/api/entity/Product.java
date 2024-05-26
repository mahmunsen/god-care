package com.godcare.api.entity;

import com.godcare.common.dto.FileResponse;
import com.godcare.common.dto.ResisterProductRequest;
import com.godcare.common.dto.UpdateProductRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@SQLDelete(sql = "UPDATE product SET is_deleted = true WHERE id = ?")
@Table(name = "product")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "any_options")
    private Boolean anyOptions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @CreationTimestamp
    @Column(name = "time_created", nullable = false, updatable = false)
    private Instant timeCreated;

    @UpdateTimestamp
    @Column(name = "time_updated", nullable = false)
    private Instant timeUpdated;

    @ColumnDefault(value = "false")
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    public static Product from(ResisterProductRequest request, Category category) {
        Long id = null;
        String name = request.getName();
        BigDecimal price = request.getPrice();
        Integer quantity = request.getQuantity();
        Boolean anyOptions = request.getAnyOptions();
        Category cat = category;
        Instant timeCreated = Instant.now();
        Instant timeUpdated = Instant.now();
        Boolean isDeleted = false;
      
        return new Product(id, name, price, quantity, anyOptions, cat, timeCreated, timeUpdated, isDeleted);
    }

    public void update(Category category, UpdateProductRequest request) {
        this.category = (category != null) ? category : this.category;
        this.timeUpdated = Instant.now();
        this.name = (request.getName() != null) ? request.getName() : this.name;
        this.price = (request.getPrice() != null) ? request.getPrice() : this.price;
        this.quantity = (request.getQuantity() != null) ? request.getQuantity() : this.quantity;
        this.anyOptions = (request.getAnyOptions() != null) ? request.getAnyOptions() : this.anyOptions;
    }
}
