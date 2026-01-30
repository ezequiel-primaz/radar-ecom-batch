package com.radarecom.radarecom.entity;

import com.radarecom.radarecom.enums.MlCategoryBatchStatus;
import com.radarecom.radarecom.enums.MlCategoryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "ML_CATEGORIES_BATCH")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MLCategoryBatch {

    @Id
    private String id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "URL")
    private String url;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private MlCategoryBatchStatus status;

    @Column(name = "CURRENT_PAGE")
    private Integer currentPage;

    @Column(name = "LAST_TOTAL_PAGE")
    private Integer lastTotalPage;

    @Column(name = "LAST_UPDATE")
    private LocalDateTime lastUpdate;

    @Column(name = "STARTED_AT")
    private LocalDateTime startedAt;

    @Column(name = "ENDED_AT")
    private LocalDateTime endedAt;

    @Column(name = "CURRENT_PROCESS_RETRIES")
    private Integer currentProcessRetries;

}
