package com.radarecom.radarecom.job;

import com.radarecom.radarecom.entity.MLJobProcess;
import com.radarecom.radarecom.enums.MLJobId;
import com.radarecom.radarecom.job.service.MLJobService;
import com.radarecom.radarecom.job.service.ScanMLCategoriesJobService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.radarecom.radarecom.enums.MLJobId.SCAN_ML_PRODUCTS;

@Component("ScanMLProductsJob")
@AllArgsConstructor
public class ScanMLProductsJob implements Job {

    private static final Logger log = LogManager.getLogger();

    private final static MLJobId ML_JOB_ID = SCAN_ML_PRODUCTS;
    private final static Integer NUMBER_OF_THREADS = 4;

    private final MLJobService MLJobService;
    private final ScanMLCategoriesJobService scanMLCategoriesJobService;

    @Override
    public MLJobId getJobId() {
        return ML_JOB_ID;
    }

    @Override
    public void start(MLJobProcess mlJobProcess) {

    }

    @Async
    @Override
    public void execute(MLJobProcess mlJobProcess) {
        //close(mlJobProcess.getTransId(), JobStatus.COMPLETED);
    }

    @Async
    @Override
    public void close(MLJobProcess mlJobProcess) {
//        if (!MLJobService.isJobAlreadyClosed(ML_JOB_ID)){
//            log.info("[{}] Job closing.", ML_JOB_ID);
//
//            MLJobService.closeJob(ML_JOB_ID, status);
//
//            log.info("[{}] Job closed. Status = [{}]", ML_JOB_ID, status);
//        }
    }

    @Override
    public void forceClose(MLJobProcess mlJobProcess) {

    }

}
