package com.vulinh.controller.impl;

import com.vulinh.controller.api.CategoryAPI;
import com.vulinh.data.dto.GenericResponse;
import com.vulinh.data.dto.category.CategoryCreationDTO;
import com.vulinh.data.dto.category.CategoryDTO;
import com.vulinh.data.dto.category.CategorySearchDTO;
import com.vulinh.service.category.CategoryService;
import com.vulinh.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategoryController implements CategoryAPI {

  private final CategoryService categoryService;

  @Override
  public GenericResponse<CategoryDTO> createCategory(CategoryCreationDTO categoryCreationDTO) {
    return GenericResponse.success(categoryService.createCategory(categoryCreationDTO));
  }

  @Override
  public GenericResponse<Page<CategoryDTO>> searchCategories(
      CategorySearchDTO categorySearchDTO, Pageable pageable) {
    return GenericResponse.success(categoryService.searchCategories(categorySearchDTO, pageable));
  }

  @Override
  public ResponseEntity<Void> deleteCategory(String categoryId) {
    return ResponseUtils.returnOkOrNoContent(categoryService.deleteCategory(categoryId));
  }
}
