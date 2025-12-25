package com.vulinh.service.category;

import module java.base;

import com.vulinh.data.dto.request.CategoryCreationRequest;
import com.vulinh.data.entity.QCategory;
import com.vulinh.data.repository.CategoryRepository;
import com.vulinh.exception.ApplicationValidationException;
import com.vulinh.exception.ResourceConflictException;
import com.vulinh.factory.ValidatorStepFactory;
import com.vulinh.locale.ServiceErrorCode;
import com.vulinh.utils.PredicateBuilder;
import com.vulinh.utils.validator.ValidatorChain;
import com.vulinh.utils.validator.ValidatorChainBuilder;
import com.vulinh.utils.validator.ValidatorStep;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryValidationService {

  static final int CATEGORY_MAX_LENGTH = 500;
  static final int CATEGORY_SLUG_MAX_LENGTH = 500;

  public static final ValidatorChain<CategoryCreationRequest> BASIC_CATEGORY_VALIDATION =
      new ValidatorChainBuilder<CategoryCreationRequest>().add(CategoryRule.values()).build();

  final CategoryRepository categoryRepository;

  public void validateCategoryCreation(CategoryCreationRequest categoryCreationRequest) {
    BASIC_CATEGORY_VALIDATION.validate(categoryCreationRequest);
  }

  public void validateCategorySlug(
      CategoryCreationRequest categoryCreationRequest, String categorySlug) {
    if (StringUtils.length(categorySlug) > CATEGORY_SLUG_MAX_LENGTH) {
      throw new ApplicationValidationException(
          "Category slug exceeded %s characters".formatted(CATEGORY_SLUG_MAX_LENGTH),
          ServiceErrorCode.MESSAGE_INVALID_CATEGORY_SLUG,
          CATEGORY_SLUG_MAX_LENGTH);
    }

    if (!availableCategory(categoryCreationRequest, categorySlug)) {
      throw ResourceConflictException.resourceConflictException(
          "Category display name %s or slug %s existed"
              .formatted(categoryCreationRequest.displayName(), categorySlug),
          ServiceErrorCode.MESSAGE_EXISTED_CATEGORY);
    }
  }

  boolean availableCategory(CategoryCreationRequest postCreationDTO, String categorySlug) {
    var qCategory = QCategory.category;

    return !categoryRepository.exists(
        PredicateBuilder.or(
            qCategory.displayName.equalsIgnoreCase(postCreationDTO.displayName()),
            qCategory.categorySlug.equalsIgnoreCase(categorySlug)));
  }

  @RequiredArgsConstructor
  @Getter
  public enum CategoryRule implements ValidatorStep<CategoryCreationRequest> {
    CATEGORY_NOT_EMPTY(
        ValidatorStepFactory.noBlankField(CategoryCreationRequest::displayName),
        "Blank category is not allowed"),
    CATEGORY_NOT_TOO_LONG(
        ValidatorStepFactory.noExceededLength(
            CategoryCreationRequest::displayName, CATEGORY_MAX_LENGTH),
        "Category exceeded %s characters".formatted(CATEGORY_MAX_LENGTH));

    final Predicate<CategoryCreationRequest> predicate;
    final String exceptionMessage;
    final ServiceErrorCode applicationError = ServiceErrorCode.MESSAGE_INVALID_CATEGORY;

    final Object[] args = {CATEGORY_MAX_LENGTH};
  }
}
