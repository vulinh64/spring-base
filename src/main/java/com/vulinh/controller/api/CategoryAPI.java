package com.vulinh.controller.api;

import com.vulinh.data.constant.EndpointConstant;
import com.vulinh.data.dto.request.CategoryCreationRequest;
import com.vulinh.data.dto.request.CategorySearchRequest;
import com.vulinh.data.dto.response.CategoryResponse;
import com.vulinh.data.dto.response.GenericResponse;
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
  GenericResponse<CategoryResponse> createCategory(
      @RequestBody CategoryCreationRequest categoryCreationRequest);

  @GetMapping(EndpointConstant.CategoryEndpoint.SEARCH)
  GenericResponse<Page<CategoryResponse>> searchCategories(
      CategorySearchRequest categorySearchRequest, Pageable pageable);

  @DeleteMapping("/{categoryId}")
  ResponseEntity<Void> deleteCategory(@PathVariable("categoryId") UUID categoryId);
}
