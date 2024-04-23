package com.godcare.api.repository;

import com.godcare.api.entity.ProductPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductPhotoRepository extends JpaRepository<ProductPhoto, Long> {
}
