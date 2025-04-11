package com.vulinh.configuration;

import com.vulinh.configuration.data.SchedulingTaskProperties;
import com.vulinh.data.constant.EnvironmentConstant;
import com.vulinh.utils.springcron.HourExpression;
import com.vulinh.utils.springcron.MinuteExpression;
import com.vulinh.utils.springcron.dto.ExpressionObject;
import com.vulinh.utils.springcron.dto.HourExpressionObject;
import com.vulinh.utils.springcron.dto.MinuteExpressionObject;
import com.vulinh.utils.springcron.dto.SpringCronGeneratorDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile(EnvironmentConstant.ENV_PRODUCTION)
@Component
@RequiredArgsConstructor
public class SchedulingTaskSupport {

  private final SchedulingTaskProperties schedulingTaskProperties;

  public String cleanExpiredUserSessionsExpression() {
    var expression = schedulingTaskProperties.cleanExpiredUserSessions();

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
