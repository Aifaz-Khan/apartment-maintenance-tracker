package com.example.maintenance.category.api;

import com.example.maintenance.category.application.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Complaint Categories", description = "Endpoints for retrieving active complaint categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(
            summary = "List active complaint categories",
            description = "Returns active global categories, or both global and society-specific categories if societyId is provided"
    )
    public ResponseEntity<List<CategoryResponse>> getActiveCategories(
            @Parameter(description = "Optional society ID to include society-specific categories")
            @RequestParam(name = "societyId", required = false) Long societyId
    ) {
        List<CategoryResponse> categories = categoryService.getActiveCategories(societyId);
        return ResponseEntity.ok(categories);
    }
}
