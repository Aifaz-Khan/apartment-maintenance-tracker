package com.example.maintenance.category.application;

import com.example.maintenance.category.api.CategoryResponse;
import com.example.maintenance.category.domain.ComplaintCategory;
import com.example.maintenance.category.infrastructure.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getActiveCategories(Long societyId) {
        List<ComplaintCategory> categories;
        if (societyId != null) {
            categories = categoryRepository.findActiveBySocietyOrGlobal(societyId);
        } else {
            categories = categoryRepository.findByActiveTrueAndSocietyIsNullOrderByNameAsc();
        }

        return categories.stream()
                .map(CategoryResponse::fromEntity)
                .toList();
    }
}
