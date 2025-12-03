package com.vulinh.configuration;

import com.vulinh.data.constant.CommonConstant;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Spring Base Project APIs", version = "v1"))
@SecurityScheme(
    name = CommonConstant.SECURITY_SCHEME_NAME,
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer")
public class OpenAPIConfiguration {}
