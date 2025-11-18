package com.vulinh.schedule;

import com.vulinh.configuration.data.ApplicationProperties;
import com.vulinh.utils.springcron.HourExpression;
import com.vulinh.utils.springcron.MinuteExpression;
import com.vulinh.utils.springcron.dto.ExpressionObject;
import com.vulinh.utils.springcron.dto.HourExpressionObject;
import com.vulinh.utils.springcron.dto.MinuteExpressionObject;
import com.vulinh.utils.springcron.dto.SpringCronGeneratorDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulingTaskSupportService {

  final ApplicationProperties applicationProperties;

  public String cleanExpiredUserSessionsExpression() {
    var expression = applicationProperties.schedule().cleanExpiredUserSessions();

    if (StringUtils.isBlank(expression)) {
      return SpringCronGeneratorDTO.builder()
          .second(ExpressionObject.NO_CARE)
          .minute(MinuteExpressionObject.of(MinuteExpression.EVERY_MINUTE))
          .hour(HourExpressionObject.of(HourExpression.EVERY_HOUR_BETWEEN, 1, 5))
          .build()
          .toCronExpression();
    }

    return expression;
  }
}
