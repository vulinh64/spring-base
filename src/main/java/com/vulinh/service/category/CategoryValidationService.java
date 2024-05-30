package com.vulinh.service.category;

import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.data.dto.category.CategoryCreationDTO;
import com.vulinh.data.dto.message.WithHttpStatusCode;
import com.vulinh.data.entity.Category_;
import com.vulinh.data.repository.CategoryRepository;
import com.vulinh.exception.ExceptionBuilder;
import com.vulinh.factory.ValidatorStepFactory;
import com.vulinh.utils.SpecificationBuilder;
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
                "Category slug is too long"))
        .executeValidation(categorySlug);

    if (!availableCategory(categoryCreationDTO, categorySlug)) {
      throw ExceptionBuilder.buildCommonException(
          "Category display name %s or slug %s existed"
              .formatted(categoryCreationDTO.displayName(), categorySlug),
          CommonMessage.MESSAGE_EXISTED_CATEGORY);
    }
  }

  public boolean availableCategory(CategoryCreationDTO postCreationDTO, String categorySlug) {
    return !categoryRepository.exists(
        SpecificationBuilder.or(
            SpecificationBuilder.eq(Category_.displayName, postCreationDTO.displayName()),
            SpecificationBuilder.eq(Category_.categorySlug, categorySlug)));
  }

  @RequiredArgsConstructor
  @Getter
  public enum CategoryRule implements ValidatorStep<CategoryCreationDTO> {
    CATEGORY_NOT_EMPTY(
        ValidatorStepFactory.noBlankField(CategoryCreationDTO::displayName),
        "Blank category is not allowed"),
    CATEGORY_NOT_TOO_LONG(
        ValidatorStepFactory.noExceededLength(CategoryCreationDTO::displayName, CATEGORY_MAX_LENGTH),
        "Category must be 500 characters or less");

    private final Predicate<CategoryCreationDTO> predicate;
    private final String additionalMessage;

    @Override
    public WithHttpStatusCode getErrorMessage() {
      return CommonMessage.MESSAGE_INVALID_CATEGORY;
    }
  }
}
