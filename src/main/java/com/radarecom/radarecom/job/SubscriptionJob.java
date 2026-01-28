package com.radarecom.radarecom.job;

import com.radarecom.radarecom.annotation.JobLogs;
import com.radarecom.radarecom.enums.JobId;
import com.radarecom.radarecom.enums.JobStatus;
import com.radarecom.radarecom.job.service.JobService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.radarecom.radarecom.constants.Constants.ORIGIN_TRANSID;
import static com.radarecom.radarecom.constants.Constants.TRANSID;
import static com.radarecom.radarecom.enums.JobId.SUBSCRIPTION_JOB;

@Component("SubscriptionJob")
@AllArgsConstructor
public class SubscriptionJob implements JobProcessor {

    private static final Logger log = LogManager.getLogger();

    private final static JobId JOB_ID = SUBSCRIPTION_JOB;

    private final static Integer NUMBER_OF_THREADS = 4;
    private final static Integer PAGE_SIZE = 40;

    private final JobService jobService;

    @Override
    public JobId getJobId() {
        return JOB_ID;
    }

    @Async
    @JobLogs(jobId = SUBSCRIPTION_JOB, addStartEndLogs = true, originTransIdArgNumber = 1)
    @Override
    public void execute(String originTransId) {
//        try {
//            var shouldExecuteJob = true;
//
//            do {
//                var page = userJobService.getPage(JOB_ID, JobStatus.NOT_STARTED, 0, PAGE_SIZE);
//
//                if (page.isEmpty()) break;
//
//                List<CompletableFuture<Void>> executions = new ArrayList<>();
//
//                var content = page.getContent();
//                var listSize = content.size();
//
//                if (listSize > 0) {
//                    // calcula o tamanho de cada bloco (arredondando pra cima)
//                    int chunkSize = (int) Math.ceil((double) listSize / NUMBER_OF_THREADS);
//
//                    int fromIndex = 0;
//                    while (fromIndex < listSize) {
//                        int toIndex = Math.min(fromIndex + chunkSize, listSize);
//
//                        var subList = content.subList(fromIndex, toIndex);
//
//                        var execution = subscriptionJobService.processUserJobList(
//                                subList,
//                                ThreadContext.get(TRANSID),
//                                ThreadContext.get(ORIGIN_TRANSID));
//
//                        executions.add(execution);
//
//                        fromIndex = toIndex;
//                    }
//                }
//
//                CompletableFuture.allOf(executions.toArray(new CompletableFuture[0])).join();
//
//                if (!page.hasNext()) shouldExecuteJob = false;
//            }while (shouldExecuteJob);
//
//        }catch (Exception e){
//            log.error("Error executing JobProcess! JobId = [{}] | message = [{}]", JOB_ID, e.getMessage());
//        }
//
//        close(originTransId);
    }

    @Async
    @JobLogs(jobId = SUBSCRIPTION_JOB, addStartEndLogs = true, originTransIdArgNumber = 1)
    @Override
    public void close(String originTransId) {
        if (!jobService.isJobAlreadyClosed(JOB_ID)){
            log.info("[{}] Job closing.", JOB_ID);

            var status = jobService.closeJob(JOB_ID);

            log.info("[{}] Job closed. Status = [{}]", JOB_ID, status);
        }
    }

}
