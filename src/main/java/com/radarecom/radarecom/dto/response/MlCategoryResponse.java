package com.radarecom.radarecom.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MlCategoryResponse {

    private String id;

    private String parentId;

    private String name;

    private List<MlCategoryResponse> children;

}
