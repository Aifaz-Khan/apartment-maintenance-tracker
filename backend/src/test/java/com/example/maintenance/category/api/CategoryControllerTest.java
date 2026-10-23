package com.example.maintenance.category.api;

import com.example.maintenance.category.application.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    @DisplayName("GET /api/v1/categories returns 200 OK and JSON array of categories")
    void getActiveCategories_returnsOkAndJsonList() throws Exception {
        CategoryResponse category = new CategoryResponse(
                1L, null, "Plumbing", "Plumbing leaks and pipes", true, true
        );

        when(categoryService.getActiveCategories(null)).thenReturn(List.of(category));

        mockMvc.perform(get("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Plumbing"))
                .andExpect(jsonPath("$[0].isGlobal").value(true))
                .andExpect(jsonPath("$[0].active").value(true));
    }
}
