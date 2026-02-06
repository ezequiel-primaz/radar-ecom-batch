package com.radarecom.radarecom.job.service;

import com.radarecom.radarecom.enums.MLJobId;
import com.radarecom.radarecom.projection.MLCategoriesBatchSummaryProjection;
import com.radarecom.radarecom.repository.MLCategoryBatchRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.radarecom.radarecom.enums.MLJobId.SCAN_ML_CATEGORIES;

@Service
@AllArgsConstructor
public class ScanMLCategoriesJobService {

    private static final Logger log = LogManager.getLogger();

    private final static MLJobId ML_JOB_ID = SCAN_ML_CATEGORIES;

    private final MLCategoryBatchRepository mlCategoryBatchRepository;

    public boolean shouldClose(){
        return !(mlCategoryBatchRepository.getCountByStatusNotStartedOrInProgress() > 0);
    }

    public MLCategoriesBatchSummaryProjection getSummaryProjection(){
        return mlCategoryBatchRepository.findSummaryProjection();
    }

    @Transactional
    public void closeAllMLCategoriesBatch(){
        try {
            mlCategoryBatchRepository.closeAllMLCategoriesBatch();
        }catch (Exception e){
            log.error("Error closing All MLCategoriesBatch.");
        }
    }

}
