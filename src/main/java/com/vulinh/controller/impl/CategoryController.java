package com.vulinh.controller.impl;

import module java.base;

import com.vulinh.controller.api.CategoryAPI;
import com.vulinh.data.dto.GenericResponse;
import com.vulinh.data.dto.request.CategoryCreationRequest;
import com.vulinh.data.dto.request.CategorySearchRequest;
import com.vulinh.data.dto.response.CategoryResponse;
import com.vulinh.data.dto.response.CategoryShortResponse;
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

  final CategoryService categoryService;

  @Override
  public GenericResponse<List<CategoryShortResponse>> getAllCategories() {
    return GenericResponse.success(categoryService.getAllCategories());
  }

  @Override
  public GenericResponse<CategoryResponse> createCategory(
      CategoryCreationRequest categoryCreationRequest) {
    return GenericResponse.success(categoryService.createCategory(categoryCreationRequest));
  }

  @Override
  public GenericResponse<Page<CategoryResponse>> searchCategories(
      CategorySearchRequest categorySearchRequest, Pageable pageable) {
    return GenericResponse.success(
        categoryService.searchWithPostCount(categorySearchRequest, pageable));
  }

  @Override
  public ResponseEntity<Void> deleteCategory(UUID categoryId) {
    return ResponseUtils.returnOkOrNoContent(categoryService.deleteCategory(categoryId));
  }
}
