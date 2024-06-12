package com.godcare.api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;


@SQLDelete(sql = "UPDATE product_photo SET is_deleted = true WHERE id = ?")
@Table(name = "product_photo")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProductPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Lob
    @Column(name = "image_url")
    private String imgUrl;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @CreationTimestamp
    @Column(name = "time_created", nullable = false, updatable = false)
    private Instant timeCreated;

    @UpdateTimestamp
    @Column(name = "time_updated", nullable = false)
    private Instant timeUpdated;

    @ColumnDefault(value = "false")
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    public static ProductPhoto from(String imageUrl) {
        Long id = null;
        String imgUrl = imageUrl;
        Long productId = null;
        Instant timeCreated = Instant.now();
        Instant timeUpdated = null;
        Boolean isDeleted = false;
        return new ProductPhoto(id, imgUrl, productId, timeCreated, timeUpdated, isDeleted);
    }

    public void update(Long productId) {
        this.productId = productId;
    }
}
