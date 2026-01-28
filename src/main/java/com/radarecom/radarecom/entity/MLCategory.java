package com.radarecom.radarecom.entity;

import com.radarecom.radarecom.enums.MlCategoryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "ML_CATEGORIES")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MLCategory {

    @EmbeddedId
    private MLCategoryID id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "URL")
    private String url;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private MlCategoryStatus status;

    @Column(name = "LEVEL")
    private Integer level;

}
