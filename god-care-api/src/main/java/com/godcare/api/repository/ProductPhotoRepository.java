package com.godcare.api.repository;

import com.godcare.api.entity.ProductPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductPhotoRepository extends JpaRepository<ProductPhoto, Long> {
    Optional<List<ProductPhoto>> findAllByProductId(Long productId);
    Optional<List<ProductPhoto>> findAllByProductIdAndIsDeletedFalse(Long productId);
    Optional<ProductPhoto> findByImgUrlAndIsDeletedFalse(String imgUrl);
    @Transactional
    @Modifying
    @Query(" DELETE from ProductPhoto p WHERE p.id =:id")
    void deletePhotoById(@Param("id") Long productPhotoId);
    @Transactional
    @Modifying
    @Query(" UPDATE ProductPhoto p SET p.productId =:id WHERE p.id =:photoId ")
    void updateProductIdByPhotoId(@Param("id") Long productId, @Param("photoId") Long productPhotoId);
}
