package com.radarecom.radarecom.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MLCategoryID {

    @Column(name = "ID")
    private String id;

    @Column(name = "PARENT_ID")
    private String parentId;

}
