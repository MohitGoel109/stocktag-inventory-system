package com.inventory.api.service;

import com.inventory.api.dto.CategoryDto;
import com.inventory.api.entity.Category;
import com.inventory.api.exception.ConflictException;
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
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public List<CategoryDto> findAll() {
        return categoryRepository.findAll().stream().map(this::toDto).toList();
    }

    public CategoryDto findById(Long id) {
        return toDto(getEntity(id));
    }

    @Transactional
    public CategoryDto create(CategoryDto dto) {
        String name = dto.name().trim();
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException("A category named '" + name + "' already exists");
        }
        Category saved = categoryRepository.save(Category.builder().name(name).build());
        return toDto(saved);
    }

    @Transactional
    public CategoryDto update(Long id, CategoryDto dto) {
        Category category = getEntity(id);
        String name = dto.name().trim();

        categoryRepository.findByNameIgnoreCase(name).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new ConflictException("A category named '" + name + "' already exists");
            }
        });

        category.setName(name);
        return toDto(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        Category category = getEntity(id);
        long productCount = productRepository.countByCategoryId(id);
        if (productCount > 0) {
            throw new ConflictException(
                    "Cannot delete '" + category.getName() + "' — " + productCount +
                    " product(s) still use this category. Reassign or delete them first.");
        }
        categoryRepository.delete(category);
    }

    private Category getEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id " + id));
    }

    private CategoryDto toDto(Category c) {
        long count = productRepository.countByCategoryId(c.getId());
        return new CategoryDto(c.getId(), c.getName(), count);
    }
}
