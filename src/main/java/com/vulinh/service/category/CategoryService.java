package com.vulinh.service.category;

import com.vulinh.constant.CommonConstant;
import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.data.dto.category.CategoryCreationDTO;
import com.vulinh.data.dto.category.CategoryDTO;
import com.vulinh.data.entity.Category;
import com.vulinh.data.mapper.CategoryMapper;
import com.vulinh.data.repository.CategoryRepository;
import com.vulinh.exception.ExceptionBuilder;
import com.vulinh.utils.PostUtils;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

  private static final CategoryMapper CATEGORY_MAPPER = CategoryMapper.INSTANCE;

  private final CategoryRepository categoryRepository;

  private final CategoryValidationService categoryValidationService;

  public Category getCategory(String categoryId) {
    return Optional.ofNullable(categoryId)
        .or(() -> Optional.of(CommonConstant.UNCATEGORIZED_ID))
        .flatMap(categoryRepository::findById)
        .orElseThrow(
            () ->
                ExceptionBuilder.buildCommonException(
                    "Invalid provided category ID: %s, or default category [%s] did not exist"
                        .formatted(categoryId, CommonConstant.UNCATEGORIZED_ID),
                    CommonMessage.MESSAGE_INVALID_CATEGORY));
  }

  @Transactional
  public void initializeFirstCategory() {
    categoryRepository
        .findById(CommonConstant.UNCATEGORIZED_ID)
        .ifPresentOrElse(
            category -> {
              var isChanged = false;

              if (!CommonConstant.UNCATEGORIZED_NAME.equals(category.getDisplayName())) {
                category.setDisplayName(CommonConstant.UNCATEGORIZED_NAME);

                isChanged = true;
              }

              if (!CommonConstant.UNCATEGORIZED_SLUG.equals(category.getCategorySlug())) {
                category.setCategorySlug(CommonConstant.UNCATEGORIZED_SLUG);

                if (!isChanged) {
                  isChanged = true;
                }
              }

              if (isChanged) {
                categoryRepository.save(category);

                log.info(
                    "Changed default category (ID {}) name to {}, slug to {}",
                    CommonConstant.UNCATEGORIZED_ID,
                    CommonConstant.UNCATEGORIZED_NAME,
                    CommonConstant.UNCATEGORIZED_SLUG);
              }
            },
            () -> {
              categoryRepository.save(
                  Category.builder()
                      .id(CommonConstant.UNCATEGORIZED_ID)
                      .categorySlug(CommonConstant.UNCATEGORIZED_SLUG)
                      .displayName(CommonConstant.UNCATEGORIZED_NAME)
                      .build());

              log.info(
                  "Initialized first category: {} - {}",
                  CommonConstant.UNCATEGORIZED_ID,
                  CommonConstant.UNCATEGORIZED_NAME);
            });
  }

  @Transactional
  public CategoryDTO createCategory(CategoryCreationDTO categoryCreationDTO) {
    categoryValidationService.validateCategoryCreation(categoryCreationDTO);

    var slug = PostUtils.createBasicSlug(categoryCreationDTO.displayName());

    categoryValidationService.validateCategorySlug(categoryCreationDTO, slug);

    return CATEGORY_MAPPER.toDto(
        categoryRepository.save(CATEGORY_MAPPER.toCategory(categoryCreationDTO, slug)));
  }
}
