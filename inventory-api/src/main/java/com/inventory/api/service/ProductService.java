package com.inventory.api.service;

import com.inventory.api.dto.ProductDto;
import com.inventory.api.entity.Category;
import com.inventory.api.entity.Product;
import com.inventory.api.exception.NotFoundException;
import com.inventory.api.repository.CategoryRepository;
import com.inventory.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<ProductDto> findAll() {
        return productRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<ProductDto> search(String term) {
        if (term == null || term.isBlank()) {
            return findAll();
        }
        return productRepository.searchByName(term.trim()).stream().map(this::toDto).toList();
    }

    public List<ProductDto> findLowStock() {
        return productRepository.findLowStock(Product.LOW_STOCK_THRESHOLD).stream().map(this::toDto).toList();
    }

    public ProductDto findById(Long id) {
        return toDto(getEntity(id));
    }

    @Transactional
    public ProductDto create(ProductDto dto) {
        Category category = getCategory(dto.categoryId());
        Product product = Product.builder()
                .name(dto.name().trim())
                .quantity(dto.quantity())
                .price(dto.price())
                .description(dto.description())
                .category(category)
                .build();
        return toDto(productRepository.save(product));
    }

    @Transactional
    public ProductDto update(Long id, ProductDto dto) {
        Product product = getEntity(id);
        Category category = getCategory(dto.categoryId());

        product.setName(dto.name().trim());
        product.setQuantity(dto.quantity());
        product.setPrice(dto.price());
        product.setDescription(dto.description());
        product.setCategory(category);

        return toDto(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        Product product = getEntity(id);
        productRepository.delete(product);
    }

    private Product getEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id " + id));
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found with id " + categoryId));
    }

    private ProductDto toDto(Product p) {
        return new ProductDto(
                p.getId(),
                p.getName(),
                p.getQuantity(),
                p.getPrice(),
                p.getDescription(),
                p.getCategory().getId(),
                p.getCategory().getName(),
                p.isLowStock()
        );
    }
}
