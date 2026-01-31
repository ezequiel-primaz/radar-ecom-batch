package com.radarecom.radarecom.entity;

import com.radarecom.radarecom.enums.MLBatchSchedulerIdAction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity(name = "ML_BATCH_SCHEDULER_ACTION")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MLBatchSchedulerAction {

    @Id
    @Column(name = "ACTION")
    @Enumerated(value = EnumType.STRING)
    private MLBatchSchedulerIdAction id;

    @Column(name = "LAST_UPDATE")
    private LocalDate lastUpdate;

}
