package com.vulinh.data.mapper;

import com.vulinh.data.base.EntityDTOMapper;
import com.vulinh.data.dto.request.CategoryCreationRequest;
import com.vulinh.data.dto.response.CategoryResponse;
import com.vulinh.data.entity.Category;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(builder = @Builder(disableBuilder = true), unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper extends EntityDTOMapper<Category, CategoryResponse> {

  CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

  @Mapping(target = "id", ignore = true)
  Category toCategory(CategoryCreationRequest categoryCreationRequest, String categorySlug);
}
