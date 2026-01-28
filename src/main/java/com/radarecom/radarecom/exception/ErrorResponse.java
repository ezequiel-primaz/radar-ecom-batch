package com.radarecom.radarecom.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private Map<String, String> errors;

    public ErrorResponse(String errorMessage){
        this.errors = Map.of("message", errorMessage);
    }

    public ErrorResponse(Map<String, String> errors){
        this.errors = errors;
    }

    public void setErrorMessage(String errorMessage){
        this.errors = Map.of("message", errorMessage);
    }

}
