package com.vulinh.data.mapper;

import com.vulinh.data.base.EntityDTOMapper;
import com.vulinh.data.dto.category.CategoryCreationDTO;
import com.vulinh.data.dto.category.CategoryDTO;
import com.vulinh.data.entity.Category;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(builder = @Builder(disableBuilder = true))
public interface CategoryMapper extends EntityDTOMapper<Category, CategoryDTO> {

  CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

  @Mapping(target = "id", ignore = true)
  Category toCategory(CategoryCreationDTO categoryCreationDTO, String categorySlug);
}
