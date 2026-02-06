package com.radarecom.radarecom.entity.jsonb;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ScanMLCategoriesSummary extends MLJobProcessSummary{

    private Integer notStarted;
    private Integer inProgress;
    private Integer completed;
    private Integer error;

}
