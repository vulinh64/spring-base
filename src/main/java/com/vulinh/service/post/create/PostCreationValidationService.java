package com.vulinh.service.post.create;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostCreationValidationService {

  public static final int TITLE_MAX_LENGTH = 5000;
  public static final int SLUG_MAX_LENGTH = 5000;
}
