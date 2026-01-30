package com.radarecom.radarecom.job;

import com.radarecom.radarecom.entity.MLJobProcess;
import com.radarecom.radarecom.enums.MLJobId;
import com.radarecom.radarecom.job.processor.CategoryProcessor;
import com.radarecom.radarecom.job.service.MLJobService;
import com.radarecom.radarecom.job.service.ScanMLCategoriesJobService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.radarecom.radarecom.constants.Constants.TRANSID;
import static com.radarecom.radarecom.enums.JobStatus.COMPLETED;
import static com.radarecom.radarecom.enums.JobStatus.IN_PROGRESS;
import static com.radarecom.radarecom.enums.MLJobId.SCAN_ML_CATEGORIES;
import static java.util.Objects.isNull;

@Component("ScanMLCategoriesJob")
@AllArgsConstructor
public class ScanMLCategoriesJob implements Job {

    private static final Logger log = LogManager.getLogger();

    private final static Integer NUMBER_OF_THREADS = 30;
    private final static Integer EXECUTE_REFRESH_MINUTES = 10;

    private final static MLJobId ML_JOB_ID = SCAN_ML_CATEGORIES;

    private final MLJobService mlJobService;
    private final CategoryProcessor categoryProcessor;
    private final ScanMLCategoriesJobService scanMLCategoriesJobService;

    @Override
    public MLJobId getJobId() {
        return ML_JOB_ID;
    }

    @Override
    public void start(MLJobProcess mlJobProcess) {
        mlJobService.startMlJobProcess(mlJobProcess);

        ThreadContext.put(TRANSID, mlJobProcess.getTransId());
        log.info("MLJob [{}] | MLJobProcess [{}] | started.", mlJobProcess.getMLJobId(), mlJobProcess.getId());
        ThreadContext.clearAll();
    }

    @Async
    @Override
    public void execute(MLJobProcess mlJobProcess) {
        ThreadContext.put(TRANSID, mlJobProcess.getTransId());

        if (mlJobProcess.getStatus().equals(IN_PROGRESS) && (isNull(mlJobProcess.getLastUpdate()) || mlJobProcess.getLastUpdate().isBefore(LocalDateTime.now().minusMinutes(EXECUTE_REFRESH_MINUTES)))){

            log.info("MLJob [{}] | MLJobProcess [{}] | Starting workers.", mlJobProcess.getMLJobId(), mlJobProcess.getId());

            for (int i = 0; i < NUMBER_OF_THREADS; i++) {
                categoryProcessor.startWorker(mlJobProcess.getTransId());
            }

            log.info("MLJob [{}] | MLJobProcess [{}] | Workers started.", mlJobProcess.getMLJobId(), mlJobProcess.getId());
        }

        ThreadContext.clearAll();
    }

    @Async
    @Override
    public void close(MLJobProcess mlJobProcess) {
        // responsavel por checkar se as condicoes para o fechamento do job estao concluidas e fechar o mesmo
        // fazer summary
        ThreadContext.put(TRANSID, mlJobProcess.getTransId());

        if (!mlJobProcess.getStatus().equals(COMPLETED) && scanMLCategoriesJobService.shouldClose()){
            log.info("MLJob [{}] | MLJobProcess [{}] | Closing.", mlJobProcess.getMLJobId(), mlJobProcess.getId());

            mlJobService.closeJob(mlJobProcess.getMLJobId(), COMPLETED);

            log.info("MLJob [{}] | MLJobProcess [{}] | Closed.", mlJobProcess.getMLJobId(), mlJobProcess.getId());
        }

        //mover categorias para not_started, last_update started_at e ended_at para null,

        ThreadContext.clearAll();
    }

    @Override
    public void forceClose(MLJobProcess mlJobProcess) {
        // caso o job nao esta fechado, fechar a forca
        // fazer summary

        ThreadContext.put(TRANSID, mlJobProcess.getTransId());

        //melhorar esse tratamento com mais tipos de status etc
        if (!mlJobProcess.getStatus().equals(COMPLETED)){
            log.info("MLJob [{}] | MLJobProcess [{}] | Force Closing.", mlJobProcess.getMLJobId(), mlJobProcess.getId());

            mlJobService.closeJob(mlJobProcess.getMLJobId(), COMPLETED);

            log.info("MLJob [{}] | MLJobProcess [{}] | Forced Closed.", mlJobProcess.getMLJobId(), mlJobProcess.getId());
        }

        //mover categorias para not_started

        ThreadContext.clearAll();
    }

}
