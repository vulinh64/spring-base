package com.vulinh.controller.impl;

import com.vulinh.controller.api.CategoryAPI;
import com.vulinh.data.dto.request.CategoryCreationRequest;
import com.vulinh.data.dto.request.CategorySearchRequest;
import com.vulinh.data.dto.response.CategoryResponse;
import com.vulinh.data.dto.response.GenericResponse;
import com.vulinh.factory.GenericResponseFactory;
import com.vulinh.service.category.CategoryService;
import com.vulinh.utils.ResponseUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategoryController implements CategoryAPI {

  private static final GenericResponseFactory RESPONSE_FACTORY = GenericResponseFactory.INSTANCE;

  private final CategoryService categoryService;

  @Override
  public GenericResponse<CategoryResponse> createCategory(
      CategoryCreationRequest categoryCreationRequest) {
    return RESPONSE_FACTORY.success(categoryService.createCategory(categoryCreationRequest));
  }

  @Override
  public GenericResponse<Page<CategoryResponse>> searchCategories(
      CategorySearchRequest categorySearchRequest, Pageable pageable) {
    return RESPONSE_FACTORY.success(
        categoryService.searchCategories(categorySearchRequest, pageable));
  }

  @Override
  public ResponseEntity<Void> deleteCategory(UUID categoryId) {
    return ResponseUtils.returnOkOrNoContent(categoryService.deleteCategory(categoryId));
  }
}
