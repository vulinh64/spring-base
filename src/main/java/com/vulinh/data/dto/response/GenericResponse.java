package com.vulinh.data.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;

@With
@Builder
@JsonInclude(Include.NON_NULL)
public record GenericResponse<T>(String errorCode, String displayMessage, T data) {}
