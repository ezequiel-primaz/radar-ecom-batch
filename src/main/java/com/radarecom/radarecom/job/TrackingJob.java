package com.radarecom.radarecom.job;

import com.radarecom.radarecom.annotation.JobLogs;
import com.radarecom.radarecom.enums.JobId;
import com.radarecom.radarecom.job.service.JobService;
import com.radarecom.radarecom.job.service.TrackingJobService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.radarecom.radarecom.enums.JobId.TRACKING_JOB;

@Component("TrackingJob")
@AllArgsConstructor
public class TrackingJob implements JobProcessor {

    private static final Logger log = LogManager.getLogger();

    private final static JobId JOB_ID = TRACKING_JOB;
    private final static Integer NUMBER_OF_THREADS = 4;

    private final JobService jobService;
    private final TrackingJobService trackingJobService;

    @Override
    public JobId getJobId() {
        return JOB_ID;
    }

    @Async
    @JobLogs(jobId = TRACKING_JOB, addStartEndLogs = true, originTransIdArgNumber = 1)
    @Override
    public void execute(String originTransId) {
        close(originTransId);
    }

    @Async
    @JobLogs(jobId = TRACKING_JOB, addStartEndLogs = true, originTransIdArgNumber = 1)
    @Override
    public void close(String originTransId) {
        if (!jobService.isJobAlreadyClosed(JOB_ID)){
            log.info("[{}] Job closing.", JOB_ID);

            var status = jobService.closeJob(JOB_ID);

            log.info("[{}] Job closed. Status = [{}]", JOB_ID, status);
        }
    }

}
