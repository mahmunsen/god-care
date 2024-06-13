package com.godcare.api.service;

import com.godcare.api.advice.annotation.TimeTrace;
import com.godcare.api.config.BucketProperties;
import com.godcare.api.entity.Category;
import com.godcare.api.entity.Product;
import com.godcare.api.entity.ProductPhoto;
import com.godcare.api.enums.FilePath;
import com.godcare.api.enums.ProductSortType;
import com.godcare.api.exception.*;
import com.godcare.api.repository.CategoryRepository;
import com.godcare.api.repository.EmProductRepository;
import com.godcare.api.repository.ProductPhotoRepository;
import com.godcare.api.repository.ProductRepository;
import com.godcare.api.util.DateUtils;
import com.godcare.api.vo.PageableRequest;
import com.godcare.api.vo.PageResponse;
import com.godcare.common.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class ProductService {

    private final CategoryRepository categoryRepository;
    private final EmProductRepository emProductRepository;
    private final BucketProperties bucketProperties;
    private final ProductRepository productRepository;
    private final ProductPhotoRepository productPhotoRepository;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 상품 등록
     *
     * @param request 상품 업로드 요청 DTO
     */
    @TimeTrace
    @Transactional
    public CompletableFuture<Product> addProduct(ResisterProductRequest request) {
        CompletableFuture<Product> productFuture = CompletableFuture.supplyAsync(() -> {
                    Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new CategoryNotFoundException());
                    Product product = Product.from(category, request);
                    productRepository.save(product);
                    return product;
                }, threadPoolTaskExecutor)
                .thenApplyAsync(product -> {
                    List<Long> productPhotoIds = request.getProductPhotoIds();
                    if (productPhotoIds.isEmpty()) throw new AtLeastOneImageRequiredException();

                    productPhotoIds.parallelStream().forEach(productPhotoId -> productPhotoRepository.updateProductIdByPhotoId(product.getId(), productPhotoId));

                    product.setStatus("COMPLETE");
                    productRepository.save(product);
                    return product;
                }, threadPoolTaskExecutor);
        return productFuture;
    }

    /**
     * 이미지 URL 업로드
     *
     * @param request 이미지 업로드 요청 DTO
     */
    public CompletableFuture<List<ProductPhoto>> uploadImgUrls(UploadPhotoRequest request) {
        CompletableFuture<List<ProductPhoto>> productPhotos = CompletableFuture
                .supplyAsync(() -> {
                    List<String> uploadFileNames = request.getProductPhotoNames();
                    List<ProductPhoto> productPhotoList = uploadFileNames.stream().map(uploadFileName -> {
                        String imageUrl = "https://kr.object.ncloudstorage.com/" + bucketProperties.getBucketName() + "/" + FilePath.PRODUCT_DIR.getPath() + "/" + uploadFileName;
                        ProductPhoto productPhoto = ProductPhoto.from(imageUrl);
                        return productPhoto;
                    }).map(productPhotoRepository::save).collect(Collectors.toList());
                    return productPhotoList;
                });
        return productPhotos;
    }

    /**
     * 특정 상품 상세 조회
     *
     * @param productId 상세 조회할 상품 ID
     */
    public CompletableFuture<ViewProductResponse> getProduct(Long productId) {
        CompletableFuture<Product> productFuture = CompletableFuture.supplyAsync(() -> {
            Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException());
            return product;
        });

        CompletableFuture<List<String>> photoUrlsFuture = productFuture.thenApplyAsync((product) -> {
            List<ProductPhoto> productPhotos = productPhotoRepository.findAllByProductId(product.getId()).orElseThrow(() -> new ProductPhotoNotFoundException());
            List<String> photoUrls = productPhotos.parallelStream().map(ProductPhoto::getImgUrl).collect(Collectors.toList());
            return photoUrls;
        });

        CompletableFuture<ViewProductResponse> viewProductResponseFuture = productFuture.thenCombine(photoUrlsFuture, (product, photoUrls) -> {
            Category category = categoryRepository.findById(product.getCategory().getId()).orElseThrow(() -> new CategoryNotFoundException());
            return new ViewProductResponse(product.getId(), photoUrls, category.getId(), category.getCategoryName(), product.getName(), product.getPrice(), product.getQuantity(), product.getAnyOptions());
        });

        return viewProductResponseFuture;
    }

    /**
     * 특정 상품의 이미지 하나 삭제
     *
     * @param imgUrlToDelete 삭제할 특정 이미지 URL
     */
    @Transactional
    public DeletePhotoResponse deletePhoto(String imgUrlToDelete) {
        // 찾기
        ProductPhoto productPhoto = productPhotoRepository.findByImgUrlAndIsDeletedFalse(imgUrlToDelete).orElseThrow(() -> new ProductPhotoNotFoundException());
        productPhotoRepository.deletePhotoById(productPhoto.getId());
        return new DeletePhotoResponse(productPhoto.getId(), imgUrlToDelete);
    }

    /**
     * 특정 상품의 정보 등록 완료, 업데이트
     *
     * @param productId 정보 업데이트 할 상품 ID
     * @param request   업데이트 요청 DTO
     */
    @Transactional
    public Product updateProduct(Long productId, UpdateProductRequest request) {
        // 찾기
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException());
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new CategoryNotFoundException());

        List<Long> productPhotoIds = request.getProductPhotoIds();
        if (productPhotoIds.isEmpty()) {
            List<ProductPhoto> productPhotos = productPhotoRepository.findAllByProductIdAndIsDeletedFalse(product.getId()).orElseThrow(() -> new ProductPhotoNotFoundException());
            if (productPhotos.isEmpty()) throw new AtLeastOneImageRequiredException();
        } else {
            productPhotoIds.parallelStream().map((productPhotoId) -> {
                ProductPhoto productPhoto = productPhotoRepository.findById(productPhotoId).orElseThrow(() -> new ProductPhotoNotFoundException());
                productPhoto.update(product.getId());
                return productPhoto;
            }).forEach(productPhotoRepository::save);
        }
        // 업데이트
        product.update(category, request);
        return productRepository.save(product);
    }

    /**
     * 특정 상품 삭제
     *
     * @param productId 삭제할 상품 ID
     */
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException());
        List<ProductPhoto> productPhotos = productPhotoRepository.findAllByProductIdAndIsDeletedFalse(product.getId()).orElseThrow(() -> new ProductPhotoNotFoundException());
        productRepository.deleteById(product.getId());
        productPhotos.stream().forEach(productPhoto -> productPhotoRepository.deleteById(productPhoto.getId()));
    }

    /**
     * 상품 리스트 전체 조회
     *
     * @param order    정렬 기준
     * @param pageable 페이지 형식
     */
    @TimeTrace
    public PageResponse<ViewProductListResponse> getProductsList(String order, PageableRequest pageable) {
        ProductSortType productSortType = ProductSortType.from(order);
        List<Product> products = null;

        switch (productSortType) {
            case CREATED_AT:
                products = (pageable.getCursor() != null) ?
                        emProductRepository.findProductsWithCursor(pageable) :
                        emProductRepository.findProducts(pageable);
                break;
            case PRICE_ASC:
                products = (pageable.getCursor() != null) ?
                        emProductRepository.findProductsByPriceAscWithCursor(pageable) :
                        emProductRepository.findProductsByPriceAsc(pageable);
                break;
            case PRICE_DESC:
                products = (pageable.getCursor() != null) ?
                        emProductRepository.findProductsByPriceDescWithCursor(pageable) :
                        emProductRepository.findProductsByPriceDesc(pageable);
                break;
            default:
                break;
        }

        List<ViewProductListResponse> viewProductListResponses = products.stream()
                .map(product -> {
                    List<ProductPhoto> productPhotos = productPhotoRepository.findAllByProductIdAndIsDeletedFalse(product.getId()).orElseThrow(() -> new ProductPhotoNotFoundException());
                    List<String> imgUrls = productPhotos.stream().map(ProductPhoto::getImgUrl).collect(Collectors.toList());

                    return new ViewProductListResponse(imgUrls, product.getCategory().getId(), product.getId(), product.getName(), product.getPrice(), product.getQuantity(), product.getAnyOptions(), DateUtils.convertToString(product.getTimeUpdated(), DateUtils.yearMonthDayHourMinuteSecond));
                })
                .collect(Collectors.toList());

        getCursorId(pageable, products);

        return PageResponse.from(viewProductListResponses, pageable);
    }

    private void getCursorId(PageableRequest pageable, List<Product> products) {
        if (products.size() > 0) pageable.setCursor(String.valueOf(products.get(products.size() - 1).getId()));
    }
}
