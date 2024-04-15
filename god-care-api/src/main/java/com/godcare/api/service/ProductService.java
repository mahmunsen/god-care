package com.godcare.api.service;

import com.godcare.api.advice.annotation.TimeTrace;
import com.godcare.api.entity.Category;
import com.godcare.api.entity.Product;
import com.godcare.api.enums.FilePath;
import com.godcare.api.enums.ProductSortType;
import com.godcare.api.exception.CategoryNotFoundException;
import com.godcare.api.exception.FileUploadFailedException;
import com.godcare.api.exception.ProductNotFoundException;
import com.godcare.api.repository.CategoryRepository;
import com.godcare.api.repository.EmProductRepository;
import com.godcare.api.repository.ProductRepository;
import com.godcare.api.vo.PageableRequest;
import com.godcare.api.vo.PageResponse;
import com.godcare.common.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final EmProductRepository emProductRepository;
    private final FileService fileService;

    // 상품 등록
    @Transactional
    public Product addProduct(ResisterProductRequest request, MultipartFile mainImg) {
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new CategoryNotFoundException());
        String filePath = FilePath.PRODUCT_DIR.getPath();
        FileResponse file = getFileUrl(mainImg, filePath);
        return productRepository.save(Product.from(request, category, file));
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

    // 상품삭제
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException());
        productRepository.deleteById(product.getId());
    }

    private FileResponse getFileUrl(MultipartFile mainImg, String filePath) {
        try {
            if (mainImg != null) {
                return fileService.uploadFile(mainImg, filePath);
            }
        } catch (Exception e) {
            throw new FileUploadFailedException();
        }
        return null;
    }

    private FileResponse getUpdatedFileUrl(MultipartFile mainImg, String filePath, String imgToDelete) {
        try {
            if (mainImg != null) {
                return fileService.updateFile(mainImg, filePath, imgToDelete);
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

        List<ViewProductListResponse> viewProductListResponses = products.stream().map(product -> new ViewProductListResponse(product.getMainImg(), product.getCategory().getId(), product.getId(), product.getName(), product.getPrice(), product.getQuantity(), product.getAnyOptions())).collect(Collectors.toList());

        getCursorId(pageable, products);

        return PageResponse.from(viewProductListResponses, pageable);
    }

    private void getCursorId(PageableRequest pageable, List<Product> products) {
        if (products.size() > 0) pageable.setCursor(products.get(products.size() - 1).getId());
    }
}
