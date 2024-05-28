package com.godcare.api.repository;

import com.godcare.api.entity.Product;
import com.godcare.api.entity.ProductPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductPhotoRepository extends JpaRepository<ProductPhoto, Long> {
    Optional<List<ProductPhoto>> findAllByProductAndIsDeletedFalse(Product product);
    Optional<ProductPhoto> findByImgUrlAndIsDeletedFalseAndProduct(String imgUrl, Product product);
}
