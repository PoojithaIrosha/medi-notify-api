package com.poojithairosha.medinotifyapi.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    @Builder.Default
    private final Instant timestamp = Instant.now();

    private final int status;
    private final String error;
    private final String message;
    private final List<String> details;
    private final String path;
}
