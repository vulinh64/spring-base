package com.vulinh.service.category;

import com.vulinh.locale.CommonMessage;
import com.vulinh.data.dto.category.CategoryCreationDTO;
import com.vulinh.data.dto.message.WithHttpStatusCode;
import com.vulinh.data.entity.QCategory;
import com.vulinh.data.repository.CategoryRepository;
import com.vulinh.factory.ExceptionFactory;
import com.vulinh.factory.ValidatorStepFactory;
import com.vulinh.utils.QueryDSLPredicateBuilder;
import com.vulinh.utils.validator.ValidatorChain;
import com.vulinh.utils.validator.ValidatorStep;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryValidationService {

  public static final int CATEGORY_MAX_LENGTH = 500;
  public static final int CATEGORY_SLUG_MAX_LENGTH = 500;

  public static final ValidatorChain<CategoryCreationDTO> BASIC_CATEGORY_VALIDATION =
      ValidatorChain.<CategoryCreationDTO>start().addValidator(CategoryRule.values());

  private final CategoryRepository categoryRepository;

  public void validateCategoryCreation(CategoryCreationDTO categoryCreationDTO) {
    BASIC_CATEGORY_VALIDATION.executeValidation(categoryCreationDTO);
  }

  public void validateCategorySlug(CategoryCreationDTO categoryCreationDTO, String categorySlug) {
    ValidatorChain.<String>start()
        .addValidator(
            ValidatorStepFactory.INSTANCE.build(
                ValidatorStepFactory.noExceededLength(
                    Function.identity(), CategoryValidationService.CATEGORY_SLUG_MAX_LENGTH),
                CommonMessage.MESSAGE_INVALID_CATEGORY_SLUG,
                "Category slug exceeded %s characters".formatted(CATEGORY_SLUG_MAX_LENGTH),
                CATEGORY_SLUG_MAX_LENGTH))
        .executeValidation(categorySlug);

    if (!availableCategory(categoryCreationDTO, categorySlug)) {
      throw ExceptionFactory.INSTANCE.buildCommonException(
          "Category display name %s or slug %s existed"
              .formatted(categoryCreationDTO.displayName(), categorySlug),
          CommonMessage.MESSAGE_EXISTED_CATEGORY);
    }
  }

  private boolean availableCategory(CategoryCreationDTO postCreationDTO, String categorySlug) {
    var qCategory = QCategory.category;

    return !categoryRepository.exists(
        QueryDSLPredicateBuilder.or(
            QueryDSLPredicateBuilder.eqic(qCategory.displayName, postCreationDTO.displayName()),
            QueryDSLPredicateBuilder.eqic(qCategory.categorySlug, categorySlug)));
  }

  @RequiredArgsConstructor
  @Getter
  public enum CategoryRule implements ValidatorStep<CategoryCreationDTO> {
    CATEGORY_NOT_EMPTY(
        ValidatorStepFactory.noBlankField(CategoryCreationDTO::displayName),
        "Blank category is not allowed"),
    CATEGORY_NOT_TOO_LONG(
        ValidatorStepFactory.noExceededLength(
            CategoryCreationDTO::displayName, CATEGORY_MAX_LENGTH),
        "Category exceeded %s characters".formatted(CATEGORY_MAX_LENGTH));

    private final Predicate<CategoryCreationDTO> predicate;
    private final String exceptionMessage;
    private final Object[] arguments = {CATEGORY_MAX_LENGTH};
    private final WithHttpStatusCode error = CommonMessage.MESSAGE_INVALID_CATEGORY;
  }
}
