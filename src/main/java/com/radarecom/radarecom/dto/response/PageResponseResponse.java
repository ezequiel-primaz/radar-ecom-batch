package com.radarecom.radarecom.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PageResponseResponse<T> {

    private int page;
    private long totalElements;
    private int totalPages;
    private List<T> content;

}
