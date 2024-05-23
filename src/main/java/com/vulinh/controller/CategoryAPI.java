package com.vulinh.controller;

import com.vulinh.constant.EndpointConstant;
import com.vulinh.data.dto.GenericResponse;
import com.vulinh.data.dto.category.CategoryCreationDTO;
import com.vulinh.data.dto.category.CategoryDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(EndpointConstant.ENDPOIT_CATEGORY)
public interface CategoryAPI {

  @PostMapping
  GenericResponse<CategoryDTO> createCategory(@RequestBody CategoryCreationDTO categoryCreationDTO);
}
