package com.vulinh.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;

@Builder
@With
@JsonInclude(Include.NON_NULL)
public record GenericResponse<T>(String code, String message, T data) {}
