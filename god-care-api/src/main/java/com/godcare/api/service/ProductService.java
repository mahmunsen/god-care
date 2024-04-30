package com.godcare.api.service;

import com.godcare.api.advice.annotation.TimeTrace;
import com.godcare.api.entity.Category;
import com.godcare.api.entity.Product;
import com.godcare.api.entity.ProductPhoto;
import com.godcare.api.enums.FilePath;
import com.godcare.api.enums.ProductSortType;
import com.godcare.api.exception.CategoryNotFoundException;
import com.godcare.api.exception.FileUploadFailedException;
import com.godcare.api.exception.ProductNotFoundException;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;


@RequiredArgsConstructor
@Service
public class ProductService {

    private final CategoryRepository categoryRepository;
    private final EmProductRepository emProductRepository;
    private final FileService fileService;
    private final ProductRepository productRepository;
    private final ProductPhotoRepository productPhotoRepository;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    // 상품 등록
    @TimeTrace
    @Transactional
    public Product addProduct(ResisterProductRequest request, List<MultipartFile> mainImgs) {

        // 작업 a
        CompletableFuture<Category> categoryFuture = CompletableFuture.supplyAsync(() ->
                categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new CategoryNotFoundException()), threadPoolTaskExecutor);

        // 작업 b -> b1
        CompletableFuture<String> filePathFuture = CompletableFuture.supplyAsync(() -> FilePath.PRODUCT_DIR.getPath());
        CompletableFuture<List<FileResponse>> filesFuture = filePathFuture.thenApplyAsync(filePath -> getFileUrls(mainImgs, filePath), threadPoolTaskExecutor);

        // 작업 a -> c
        CompletableFuture<Product> productFuture = categoryFuture.thenApplyAsync(category ->
                productRepository.save(Product.from(request, category)), threadPoolTaskExecutor
        );

        // b1, c -> b2
        CompletableFuture<List<ProductPhoto>> productPhotoFuture = filesFuture
                .thenCombineAsync(productFuture, (files, product) -> files
                        .parallelStream()
                        .map(fileResponse -> ProductPhoto.from(fileResponse, product))
                        .collect(Collectors.toList()), threadPoolTaskExecutor)
                .thenApply((productPhotos) ->
                        productPhotoRepository.saveAll(productPhotos)
                );

        CompletableFuture<Product> combinedFuture = CompletableFuture.allOf(categoryFuture, productPhotoFuture, productFuture)
                .thenApplyAsync(v -> productFuture.join(), threadPoolTaskExecutor);

        // 모든 작업이 완료될 때까지 대기하고 최종 product 반환
        return combinedFuture.join();
    }

        // 특정 상품 조회
    public ViewProductResponse getProduct(Long productId) {
        Product product = productRepository.findByIdAndIsDeletedFalse(productId).orElseThrow(() -> new ProductNotFoundException());
        Category category = categoryRepository.findById(product.getCategory().getId()).orElseThrow(() -> new CategoryNotFoundException());
        return new ViewProductResponse(product.getId(), product.getMainImg(), category.getId(), category.getCategoryName(), product.getName(), product.getPrice(), product.getQuantity(), product.getAnyOptions());
    }

    // 상품 수정
    @Transactional
    public Product updateProduct(Long productId, UpdateProductRequest request, MultipartFile mainImg) {
        // 찾기
        Product product = productRepository.findByIdAndIsDeletedFalse(productId).orElseThrow(() -> new ProductNotFoundException());
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new CategoryNotFoundException());

        String getImg = product.getMainImg();
        String imgToDelete = getImg.substring(getImg.lastIndexOf("/") + 1);
        String filePath = FilePath.PRODUCT_DIR.getPath();
        FileResponse file = getUpdatedFileUrl(mainImg, filePath, imgToDelete);

        // 업데이트
        product.update(file, category, request);
        return productRepository.save(product);
    }

    // 상품 삭제
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException());
        productRepository.deleteById(product.getId());
    }

    private List<FileResponse> getFileUrls(List<MultipartFile> mainImgs, String filePath) {
        try {
            if (mainImgs != null) {
                return fileService.uploadFiles(mainImgs, filePath);
            }
        } catch (Exception e) {
            throw new FileUploadFailedException();
        }
        return null;
    }

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

        List<ViewProductListResponse> viewProductListResponses = products.stream().map(product -> new ViewProductListResponse(product.getMainImg(), product.getCategory().getId(), product.getId(), product.getName(), product.getPrice(), product.getQuantity(), product.getAnyOptions(), DateUtils.convertToString(product.getTimeUpdated(), DateUtils.yearMonthDayHourMinuteSecond))).collect(Collectors.toList());

        getCursorId(pageable, products);

        return PageResponse.from(viewProductListResponses, pageable);
    }

    private void getCursorId(PageableRequest pageable, List<Product> products) {
        if (products.size() > 0) pageable.setCursor(String.valueOf(products.get(products.size() - 1).getId()));
    }
}
