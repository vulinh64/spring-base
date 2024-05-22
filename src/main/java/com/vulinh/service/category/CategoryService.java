package com.vulinh.service.category;

import com.vulinh.constant.CommonConstant;
import com.vulinh.data.dto.bundle.CommonMessage;
import com.vulinh.data.entity.Category;
import com.vulinh.data.repository.CategoryRepository;
import com.vulinh.exception.CommonException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public Category getCategory(String categoryId) {
    return Optional.ofNullable(categoryId)
        .or(() -> Optional.of(CommonConstant.UNCATEGORIZED_ID))
        .flatMap(categoryRepository::findById)
        .orElseThrow(
            () ->
                new CommonException(
                    "Invalid provided category ID: %s, or default category [%s] did not exist"
                        .formatted(categoryId, CommonConstant.UNCATEGORIZED_ID),
                    CommonMessage.MESSAGE_POST_INVALID_CATEGORY));
  }

  @Transactional
  public void initializeFirstCategory() {
    categoryRepository
        .findById(CommonConstant.UNCATEGORIZED_ID)
        .ifPresentOrElse(
            category -> {
              if (!CommonConstant.UNCATEGORIZED_NAME.equals(category.getDisplayName())) {
                category.setDisplayName(CommonConstant.UNCATEGORIZED_NAME);

                categoryRepository.save(category);

                log.info(
                    "Changed default category (ID {}) name to {}",
                    CommonConstant.UNCATEGORIZED_ID,
                    CommonConstant.UNCATEGORIZED_NAME);
              }
            },
            () -> {
              categoryRepository.save(
                  Category.builder()
                      .id(CommonConstant.UNCATEGORIZED_ID)
                      .displayName(CommonConstant.UNCATEGORIZED_NAME)
                      .build());

              log.info(
                  "Initialized first category: {} - {}",
                  CommonConstant.UNCATEGORIZED_ID,
                  CommonConstant.UNCATEGORIZED_NAME);
            });
  }
}
