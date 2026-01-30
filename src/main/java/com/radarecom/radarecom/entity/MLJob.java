package com.radarecom.radarecom.entity;

import com.radarecom.radarecom.enums.MLJobId;
import com.radarecom.radarecom.enums.JobStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "ML_JOBS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MLJob {

    @Id
    @Enumerated(value = EnumType.STRING)
    private MLJobId id;

    @Column(name = "CURRENT_ML_JOB_PROCESS_ID")
    private Integer currentMLJobProcessId;

    @Column(name = "STATUS")
    @Enumerated(value = EnumType.STRING)
    private JobStatus status;

    @Column(name = "IS_ACTIVE")
    private boolean isActive;

}
