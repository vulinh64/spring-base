package com.vulinh.controller.impl;

import module java.base;

import com.vulinh.controller.api.CategoryAPI;
import com.vulinh.data.dto.request.CategoryCreationRequest;
import com.vulinh.data.dto.request.CategorySearchRequest;
import com.vulinh.data.dto.response.CategoryResponse;
import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.data.dto.response.GenericResponse.ResponseCreator;
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
  public GenericResponse<CategoryResponse> createCategory(
      CategoryCreationRequest categoryCreationRequest) {
    return ResponseCreator.success(categoryService.createCategory(categoryCreationRequest));
  }

  @Override
  public GenericResponse<Page<CategoryResponse>> searchCategories(
      CategorySearchRequest categorySearchRequest, Pageable pageable) {
    return ResponseCreator.success(categoryService.search(categorySearchRequest, pageable));
  }

  @Override
  public ResponseEntity<Void> deleteCategory(UUID categoryId) {
    return ResponseUtils.returnOkOrNoContent(categoryService.deleteCategory(categoryId));
  }
}
