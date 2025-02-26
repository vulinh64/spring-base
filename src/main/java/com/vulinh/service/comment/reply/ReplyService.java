package com.vulinh.service.comment.reply;

import com.vulinh.data.dto.comment.NewCommentReplyDTO;
import com.vulinh.data.dto.user.UserBasicDTO;
import com.vulinh.data.mapper.ReplyMapper;
import com.vulinh.data.repository.ReplyRepository;
import com.vulinh.factory.ExceptionFactory;
import com.vulinh.service.comment.NewCommentValidationService;
import com.vulinh.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReplyService {

  private final NewCommentValidationService newCommentValidationService;

  private final ReplyRepository replyRepository;

  public void createReply(
      NewCommentReplyDTO newCommentReplyDTO,
      UUID commentId,
      HttpServletRequest httpServletRequest) {
    var comment = newCommentValidationService.validateReply(newCommentReplyDTO, commentId);

    var createdBy =
        SecurityUtils.getUserDTO(httpServletRequest)
            .map(UserBasicDTO::user)
            .orElseThrow(ExceptionFactory.INSTANCE::invalidAuthorization);

    replyRepository.save(
        ReplyMapper.INSTANCE.fromNewCommentReply(newCommentReplyDTO, comment, createdBy));
  }
}
