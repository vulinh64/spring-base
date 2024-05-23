package com.vulinh.controller.impl;

import com.vulinh.controller.CategoryAPI;
import com.vulinh.data.dto.GenericResponse;
import com.vulinh.data.dto.category.CategoryCreationDTO;
import com.vulinh.data.dto.category.CategoryDTO;
import com.vulinh.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategoryController implements CategoryAPI {

  private final CategoryService categoryService;

  @Override
  public GenericResponse<CategoryDTO> createCategory(CategoryCreationDTO categoryCreationDTO) {
    return GenericResponse.success(categoryService.createCategory(categoryCreationDTO));
  }
}
