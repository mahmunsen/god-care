package com.godcare.api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.Instant;

@SQLDelete(sql = "UPDATE product SET is_deleted = true WHERE id = ?")
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
    @Column(name = "original_name")
    private String originalName;

    @Lob
    @Column(name = "image_url")
    private String imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @CreationTimestamp
    @Column(name = "time_created", nullable = false, updatable = false)
    private Instant timeCreated;

    @UpdateTimestamp
    @Column(name = "time_updated", insertable = false)
    private Instant timeUpdated;

    @ColumnDefault(value = "false")
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    public static ProductPhoto from(String origName, String imageUrl, Product product) {
        Long id = null;
        String originalName = origName;
        String imgUrl = imageUrl;
        Product pro = product;
        Instant timeCreated = Instant.now();
        Instant timeUpdated = null;
        Boolean isDeleted = false;
        return new ProductPhoto(id, originalName, imgUrl, pro, timeCreated, timeUpdated, isDeleted);
    }
}
