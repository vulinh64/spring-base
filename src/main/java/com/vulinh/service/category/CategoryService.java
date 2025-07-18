package com.vulinh.service.category;

import com.querydsl.core.types.Predicate;
import com.vulinh.data.constant.CommonConstant;
import com.vulinh.data.dto.request.CategoryCreationRequest;
import com.vulinh.data.dto.request.CategorySearchRequest;
import com.vulinh.data.dto.response.CategoryResponse;
import com.vulinh.data.entity.Category;
import com.vulinh.data.entity.QCategory;
import com.vulinh.data.mapper.CategoryMapper;
import com.vulinh.data.repository.CategoryRepository;
import com.vulinh.exception.NoSuchPermissionException;
import com.vulinh.exception.NotFoundException;
import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.utils.PageableQueryService;
import com.vulinh.utils.PredicateBuilder;
import com.vulinh.utils.post.SlugUtils;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService
    implements PageableQueryService<Category, CategoryResponse, CategorySearchRequest> {

  private static final CategoryMapper CATEGORY_MAPPER = CategoryMapper.INSTANCE;

  private final CategoryRepository categoryRepository;

  private final CategoryValidationService categoryValidationService;

  public Category getCategory(UUID categoryId) {
    return Optional.ofNullable(categoryId)
        .or(() -> Optional.of(CommonConstant.UNCATEGORIZED_ID))
        .flatMap(categoryRepository::findById)
        .orElseThrow(
            () ->
                NotFoundException.entityNotFound(
                    CommonConstant.CATEGORY_ENTITY,
                    categoryId,
                    ServiceErrorCode.MESSAGE_INVALID_ENTITY_ID));
  }

  @Transactional
  public CategoryResponse createCategory(CategoryCreationRequest categoryCreationRequest) {
    categoryValidationService.validateCategoryCreation(categoryCreationRequest);

    var slug = SlugUtils.createBasicSlug(categoryCreationRequest.displayName());

    categoryValidationService.validateCategorySlug(categoryCreationRequest, slug);

    return CATEGORY_MAPPER.toDto(
        categoryRepository.save(CATEGORY_MAPPER.toCategory(categoryCreationRequest, slug)));
  }

  @Transactional
  public boolean deleteCategory(UUID categoryId) {
    if (CommonConstant.UNCATEGORIZED_ID.equals(categoryId)) {
      throw NoSuchPermissionException.noSuchPermissionException(
          "Cannot delete default category", ServiceErrorCode.MESSAGE_DEFAULT_CATEGORY_IMMORTAL);
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

  /*
   * Pageable category query implementation
   */

  @Override
  @NonNull
  public CategoryResponse toDto(@NonNull Category category) {
    return CATEGORY_MAPPER.toDto(category);
  }

  @Override
  public Predicate toPredicate(@NonNull CategorySearchRequest categorySearchRequest) {
    QCategory qCategory = QCategory.category;

    return PredicateBuilder.and(
        PredicateBuilder.likeIgnoreCase(qCategory.displayName, categorySearchRequest.displayName()),
        PredicateBuilder.likeIgnoreCase(qCategory.categorySlug, categorySearchRequest.slug()));
  }

  @Override
  @NonNull
  public QuerydslPredicateExecutor<Category> getDslRepository() {
    return categoryRepository;
  }
}
