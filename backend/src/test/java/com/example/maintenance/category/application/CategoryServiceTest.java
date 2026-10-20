package com.example.maintenance.category.application;

import com.example.maintenance.category.api.CategoryResponse;
import com.example.maintenance.category.domain.ComplaintCategory;
import com.example.maintenance.category.infrastructure.CategoryRepository;
import com.example.maintenance.society.domain.Society;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(categoryRepository);
    }

    @Test
    @DisplayName("getActiveCategories returns global categories when societyId is null")
    void getActiveCategories_whenSocietyIdNull_returnsGlobalCategories() {
        ComplaintCategory plumbing = new ComplaintCategory(null, "Plumbing", "Pipes and taps");
        plumbing.setId(1L);

        when(categoryRepository.findByActiveTrueAndSocietyIsNullOrderByNameAsc())
                .thenReturn(List.of(plumbing));

        List<CategoryResponse> results = categoryService.getActiveCategories(null);

        assertThat(results).hasSize(1);
        CategoryResponse response = results.get(0);
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Plumbing");
        assertThat(response.isGlobal()).isTrue();
        assertThat(response.societyId()).isNull();

        verify(categoryRepository).findByActiveTrueAndSocietyIsNullOrderByNameAsc();
    }

    @Test
    @DisplayName("getActiveCategories queries society-scoped and global categories when societyId is provided")
    void getActiveCategories_whenSocietyIdProvided_returnsCombinedCategories() {
        Long societyId = 10L;
        Society society = new Society();
        society.setId(societyId);

        ComplaintCategory global = new ComplaintCategory(null, "Plumbing", "Global");
        global.setId(1L);
        ComplaintCategory societyCustom = new ComplaintCategory(society, "Solar Water Heater", "Society custom");
        societyCustom.setId(2L);

        when(categoryRepository.findActiveBySocietyOrGlobal(societyId))
                .thenReturn(List.of(global, societyCustom));

        List<CategoryResponse> results = categoryService.getActiveCategories(societyId);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).isGlobal()).isTrue();
        assertThat(results.get(1).isGlobal()).isFalse();
        assertThat(results.get(1).societyId()).isEqualTo(societyId);

        verify(categoryRepository).findActiveBySocietyOrGlobal(societyId);
    }
}
