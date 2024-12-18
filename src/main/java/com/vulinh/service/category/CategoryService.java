package com.vulinh.service.category;

import com.vulinh.constant.CommonConstant;
import com.vulinh.data.dto.category.CategoryCreationDTO;
import com.vulinh.data.dto.category.CategoryDTO;
import com.vulinh.data.dto.category.CategorySearchDTO;
import com.vulinh.data.entity.Category;
import com.vulinh.data.entity.QCategory;
import com.vulinh.data.mapper.CategoryMapper;
import com.vulinh.data.repository.CategoryRepository;
import com.vulinh.factory.ExceptionFactory;
import com.vulinh.locale.CommonMessage;
import com.vulinh.utils.PredicateBuilder;
import com.vulinh.utils.post.SlugUtils;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

  private static final CategoryMapper CATEGORY_MAPPER = CategoryMapper.INSTANCE;

  private static final ExceptionFactory EXCEPTION_FACTORY = ExceptionFactory.INSTANCE;

  private final CategoryRepository categoryRepository;

  private final CategoryValidationService categoryValidationService;

  public Category getCategory(UUID categoryId) {
    return Optional.ofNullable(categoryId)
        .or(() -> Optional.of(CommonConstant.UNCATEGORIZED_ID))
        .flatMap(categoryRepository::findById)
        .orElseThrow(
            () ->
                EXCEPTION_FACTORY.buildCommonException(
                    "Invalid provided category ID: %s, or default category [%s] did not exist"
                        .formatted(categoryId, CommonConstant.UNCATEGORIZED_ID),
                    CommonMessage.MESSAGE_INVALID_CATEGORY));
  }

  @Transactional
  public CategoryDTO createCategory(CategoryCreationDTO categoryCreationDTO) {
    categoryValidationService.validateCategoryCreation(categoryCreationDTO);

    var slug = SlugUtils.createBasicSlug(categoryCreationDTO.displayName());

    categoryValidationService.validateCategorySlug(categoryCreationDTO, slug);

    return CATEGORY_MAPPER.toDto(
        categoryRepository.save(CATEGORY_MAPPER.toCategory(categoryCreationDTO, slug)));
  }

  @Transactional
  public Page<CategoryDTO> searchCategories(
      CategorySearchDTO categorySearchDTO, Pageable pageable) {
    QCategory qCategory = QCategory.category;

    return categoryRepository
        .findAll(
            PredicateBuilder.and(
                PredicateBuilder.likeIgnoreCase(
                    qCategory.displayName, categorySearchDTO.displayName()),
                PredicateBuilder.likeIgnoreCase(
                    qCategory.categorySlug, categorySearchDTO.categorySlug())),
            pageable)
        .map(CATEGORY_MAPPER::toDto);
  }

  @Transactional
  public boolean deleteCategory(UUID categoryId) {
    if (CommonConstant.UNCATEGORIZED_ID.equals(categoryId)) {
      throw EXCEPTION_FACTORY.buildCommonException(
          "Cannot delete default category", CommonMessage.MESSAGE_DEFAULT_CATEGORY_IMMORTAL);
    }

    return categoryRepository
        .findById(categoryId)
        .map(
            category -> {
              categoryRepository.delete(category);

              return true;
            })
        .isPresent();
  }
}
