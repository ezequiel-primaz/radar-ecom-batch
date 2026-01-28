package com.radarecom.radarecom.integration.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MLType {

    @JsonProperty("configuration")
    private MLExceptionByCategoryConfiguration configuration;

    @JsonProperty("exceptions_by_category")
    private List<MLExceptionByCategory> exceptionsByCategory;

}
