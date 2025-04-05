package com.vulinh.data.dto.comment;

import lombok.Builder;
import lombok.With;

@With
@Builder
public record NewCommentDTO(String content) {}
