package com.vulinh.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtils {

  public static ObjectMapper delegate() {
    return MAPPER;
  }

  @SneakyThrows
  public static <T> String toMinimizedJSON(T t) {
    return toJSONString(t, false);
  }

  @SneakyThrows
  public static <T> String toPrettyJSON(T t) {
    return toJSONString(t, true);
  }

  public static <T> T toObject(String message, Class<T> clazz) throws JsonProcessingException {
    return MAPPER.readValue(message, clazz);
  }

  private static <T> String toJSONString(T t, boolean isPretty) throws JsonProcessingException {
    return isPretty ? PRETTY_WRITER.writeValueAsString(t) : MAPPER.writeValueAsString(t);
  }

  private static final ObjectMapper MAPPER;
  private static final ObjectWriter PRETTY_WRITER;

  static {
    MAPPER =
        JsonMapper.builder()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
            .addModule(new JavaTimeModule())
            .serializationInclusion(Include.NON_NULL)
            .build();

    PRETTY_WRITER =
        MAPPER.writer(
            new DefaultPrettyPrinter()
                .withObjectIndenter(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE)
                .withArrayIndenter(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE));
  }
}
