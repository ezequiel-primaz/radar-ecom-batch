package com.radarecom.radarecom.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.radarecom.radarecom.entity.jsonb.MLJobProcessSummary;
import com.radarecom.radarecom.entity.jsonb.ScanMLCategoriesSummary;
import com.radarecom.radarecom.enums.MLJobId;
import com.radarecom.radarecom.enums.JobStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity(name = "ML_JOB_PROCESS")
@Getter
@Setter
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

    @Column(name = "SUMMARY")
    @JdbcTypeCode(SqlTypes.JSON)
    private Object summary;

    public MLJobProcessSummary getSummary() {
        try {
            var objectMapper = new ObjectMapper();
            switch (getMLJobId()){
                case SCAN_ML_CATEGORIES -> {
                    var value = objectMapper.readValue(summary.toString(), ScanMLCategoriesSummary.class);
                    return value;
                }
                case SCAN_ML_PRODUCTS -> {
                    return null;
                }
            }
            return null;
        }catch (JsonProcessingException e){
            return null;
        }
    }

    public void setSummary(MLJobProcessSummary summary) {
        try {
            var objectMapper = new ObjectMapper();
            this.summary = objectMapper
                    .writerFor(summary.getClass())
                    .writeValueAsString(summary);
        }catch (JsonProcessingException e){
            System.out.println("banana");
        }
    }


}
