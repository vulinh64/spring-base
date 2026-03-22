package com.vulinh.service.category;

import module java.base;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vulinh.data.constant.CommonConstant;
import com.vulinh.data.constant.EntityType;
import com.vulinh.data.dto.request.CategoryCreationRequest;
import com.vulinh.data.dto.request.CategorySearchRequest;
import com.vulinh.data.dto.response.CategoryResponse;
import com.vulinh.data.entity.Category;
import com.vulinh.data.entity.QCategory;
import com.vulinh.data.entity.QPost;
import com.vulinh.data.mapper.CategoryMapper;
import com.vulinh.data.projection.CategoryPostCountProjection;
import com.vulinh.data.repository.CategoryRepository;
import com.vulinh.exception.NoSuchPermissionException;
import com.vulinh.exception.NotFound404Exception;
import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.service.PageableQueryService;
import com.vulinh.utils.PredicateBuilder;
import com.vulinh.utils.post.SlugUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService
    implements PageableQueryService<Category, CategoryResponse, CategorySearchRequest> {

  static final CategoryMapper CATEGORY_MAPPER = CategoryMapper.INSTANCE;

  @PersistenceContext final EntityManager entityManager;

  final CategoryRepository categoryRepository;

  final CategoryValidationService categoryValidationService;

  public Category getCategory(UUID categoryId) {
    return Optional.ofNullable(categoryId)
        .or(() -> Optional.of(CommonConstant.NIL_UUID))
        .flatMap(categoryRepository::findById)
        .orElseThrow(
            () ->
                NotFound404Exception.entityNotFound(
                    EntityType.CATEGORY, categoryId, ServiceErrorCode.MESSAGE_INVALID_ENTITY_ID));
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
    if (CommonConstant.NIL_UUID.equals(categoryId)) {
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

  public Page<CategoryResponse> searchWithPostCount(
      CategorySearchRequest request, Pageable pageable) {
    var c = QCategory.category;
    var p = QPost.post;

    var queryFactory = new JPAQueryFactory(entityManager);

    var fetchQuery =
        queryFactory
            .select(
                Projections.constructor(
                    CategoryPostCountProjection.class,
                    c.id,
                    c.categorySlug,
                    c.displayName,
                    p.id.count()))
            .from(c)
            .leftJoin(p)
            .on(p.category.eq(c))
            .where(
                PredicateBuilder.likeIgnoreCase(c.displayName, request.displayName()),
                PredicateBuilder.likeIgnoreCase(c.categorySlug, request.slug()))
            .groupBy(c.id, c.categorySlug, c.displayName)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize());

    var countQuery =
        queryFactory
            .select(c.count())
            .from(c)
            .where(
                PredicateBuilder.and(
                    PredicateBuilder.likeIgnoreCase(c.displayName, request.displayName()),
                    PredicateBuilder.likeIgnoreCase(c.categorySlug, request.slug())));

    var results =
        fetchQuery.fetch().stream()
            .map(
                projection ->
                    CategoryResponse.builder()
                        .id(projection.id())
                        .categorySlug(projection.categorySlug())
                        .displayName(projection.displayName())
                        .postCount(projection.postCount())
                        .build())
            .toList();

    return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
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
  public Predicate buildSearchPredicate(@NonNull CategorySearchRequest categorySearchRequest) {
    var qCategory = QCategory.category;

    return PredicateBuilder.and(
        PredicateBuilder.likeIgnoreCase(qCategory.displayName, categorySearchRequest.displayName()),
        PredicateBuilder.likeIgnoreCase(qCategory.categorySlug, categorySearchRequest.slug()));
  }

  @Override
  @NonNull
  public CategoryRepository getRepository() {
    return categoryRepository;
  }
}
