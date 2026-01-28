package com.radarecom.radarecom.entity;

import com.radarecom.radarecom.enums.JobId;
import com.radarecom.radarecom.enums.JobStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "JOBS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @Enumerated(value = EnumType.STRING)
    private JobId id;

    @Column(name = "LAST_UPDATE")
    private LocalDateTime lastUpdate;

    @Column(name = "CURRENT_JOB_PROCESS_ID")
    private Integer currentJobProcessId;

    @Column(name = "STATUS")
    @Enumerated(value = EnumType.STRING)
    private JobStatus status;

    @Column(name = "IS_ACTIVE")
    private boolean isActive;

}
