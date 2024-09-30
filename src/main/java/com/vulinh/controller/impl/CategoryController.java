package com.vulinh.controller.impl;

import com.vulinh.controller.api.CategoryAPI;
import com.vulinh.data.dto.GenericResponse;
import com.vulinh.data.dto.category.CategoryCreationDTO;
import com.vulinh.data.dto.category.CategoryDTO;
import com.vulinh.data.dto.category.CategorySearchDTO;
import com.vulinh.factory.GenericResponseFactory;
import com.vulinh.service.category.CategoryService;
import com.vulinh.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Category controller", description = "Manage post categories")
public class CategoryController implements CategoryAPI {

  private static final GenericResponseFactory RESPONSE_FACTORY = GenericResponseFactory.INSTANCE;

  private final CategoryService categoryService;

  @Override
  public GenericResponse<CategoryDTO> createCategory(CategoryCreationDTO categoryCreationDTO) {
    return RESPONSE_FACTORY.success(categoryService.createCategory(categoryCreationDTO));
  }

  @Override
  public GenericResponse<Page<CategoryDTO>> searchCategories(
      CategorySearchDTO categorySearchDTO, Pageable pageable) {
    return RESPONSE_FACTORY.success(categoryService.searchCategories(categorySearchDTO, pageable));
  }

  @Override
  public ResponseEntity<Void> deleteCategory(UUID categoryId) {
    return ResponseUtils.returnOkOrNoContent(categoryService.deleteCategory(categoryId));
  }
}
