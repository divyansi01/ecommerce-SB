package com.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema
@Data
public class ErrorDetail {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("errorMessage")
    private String message;
    @JsonProperty("expectedReason")
    private String reason;
}
