package com.radarecom.radarecom.entity;

import com.radarecom.radarecom.enums.MLJobId;
import com.radarecom.radarecom.enums.JobStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "ML_JOB_PROCESS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MLJobProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ML_JOB_ID")
    @Enumerated(value = EnumType.STRING)
    private MLJobId MLJobId;

    @Column(name = "STATUS")
    @Enumerated(value = EnumType.STRING)
    private JobStatus status;

    @Column(name = "LAST_UPDATE")
    private LocalDateTime lastUpdate;

    @Column(name = "START_AT")
    private LocalDateTime startAt;

    @Column(name = "END_AT")
    private LocalDateTime endAt;

    @Column(name = "TRANS_ID")
    private String transId;

    @Column(name = "RETRIES")
    private Integer retries;

}
