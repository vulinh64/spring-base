package com.vulinh.controller.api;

import com.vulinh.constant.EndpointConstant;
import com.vulinh.data.dto.GenericResponse;
import com.vulinh.data.dto.category.CategoryCreationDTO;
import com.vulinh.data.dto.category.CategoryDTO;
import com.vulinh.data.dto.category.CategorySearchDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(EndpointConstant.ENDPOINT_CATEGORY)
@Tag(name = "Category controller", description = "Manage post categories")
public interface CategoryAPI {

  @PostMapping
  GenericResponse<CategoryDTO> createCategory(@RequestBody CategoryCreationDTO categoryCreationDTO);

  @GetMapping(EndpointConstant.CategoryEndpoint.SEARCH)
  GenericResponse<Page<CategoryDTO>> searchCategories(
      CategorySearchDTO categorySearchDTO, Pageable pageable);

  @DeleteMapping("/{categoryId}")
  ResponseEntity<Void> deleteCategory(@PathVariable("categoryId") UUID categoryId);
}
