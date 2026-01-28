package com.radarecom.radarecom.job.scheduler;

import com.radarecom.radarecom.entity.Job;
import com.radarecom.radarecom.entity.JobProcess;
import com.radarecom.radarecom.enums.JobId;
import com.radarecom.radarecom.exception.NotFoundException;
import com.radarecom.radarecom.job.JobProcessor;
import com.radarecom.radarecom.job.service.JobService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.radarecom.radarecom.constants.Constants.TRANSID;
import static com.radarecom.radarecom.enums.JobStatus.COMPLETED;
import static com.radarecom.radarecom.enums.JobStatus.ERROR;
import static java.util.Objects.isNull;

@Component
@AllArgsConstructor
public class Scheduler {

    private static final Logger log = LogManager.getLogger();

    private final List<JobProcessor> processors;

    private final JobService jobService;

    @Async
    @Scheduled(cron = "0 10 0 * * *", zone = "America/Sao_Paulo") // roda 1 vez as 00:10
    //@Scheduled(fixedRate = 600000000) || ao executar a app.
    public void jobsStart() {
        log.info("Starting Jobs!");
        var jobs = jobService.getAllActiveJobs();

        var jobsProcess = new ArrayList<JobProcess>();

        jobs.forEach(job -> {

            if (!jobService.canStartJob(job)) {
                log.error("Cant start Job ID = [{}] | Status = [{}]", job.getId(), job.getStatus());
                return;
            }

            var jobProcess = jobService.createJobProcess(job.getId(), ThreadContext.get(TRANSID));
            jobsProcess.add(jobProcess);
            jobService.startJob(job, jobProcess.getId());

            log.info("Job Started! Job ID = [{}] | JobProcess = [{}]", job.getId(), jobProcess.getId());
        });

        jobsExecute();
    }

    @Async
    @Scheduled(fixedRate = 30 * 60 * 1000, initialDelay = 30 * 60 * 1000) // roda a cada 30 min || espera 30 min pra comecar ao startar
    //@Scheduled(initialDelay = 120000, fixedRate = 120000) || roda de 2 em 2 min
    public void jobsExecute() {

        var jobProcesses = getJobProcesses();

        jobProcesses.forEach(jobProcess -> {
            try {
                if(shouldExecuteJobProcess(jobProcess)){

                    log.info("Executing JobProcess! JobId = [{}] | JobProcessId = [{}]", jobProcess.getJobId(), jobProcess.getId());

                    var jobProcessor = getJobProcessor(jobProcess.getJobId());

                    jobProcessor.execute(jobProcess.getTransId());
                }
            }catch (Exception e){
                log.error("Error executing JobProcess! JobId = [{}] | JobProcessId = [{}] | message = [{}]", jobProcess.getJobId(), jobProcess.getId(), e.getMessage());
            }

        });
    }

    @Async
    @Scheduled(cron = "0 0 23 * * *", zone = "America/Sao_Paulo") // roda 1 vez as 23:00
    //@Scheduled(initialDelay = 600000) || espera 10 min pra rodar ao startar
    public void jobsEnd() {

        var jobProcesses = getJobProcesses();

        jobProcesses.forEach(jobProcess -> {
            try {
                log.info("Closing JobProcess! JobId = [{}] | JobProcessId = [{}]", jobProcess.getJobId(), jobProcess.getId());

                getJobProcessor(jobProcess.getJobId()).close(jobProcess.getTransId());
            }catch (Exception e){
                log.error("Error closing JobProcess! JobId = [{}] | JobProcessId = [{}]", jobProcess.getJobId(), jobProcess.getId());
            }
        });
    }

    private boolean shouldExecuteJobProcess(JobProcess jobProcess){
        if (jobProcess.getStatus().equals(COMPLETED) || jobProcess.getStatus().equals(ERROR)) return false;
        return isNull(jobProcess.getLastUpdate()) || jobProcess.getLastUpdate().plusMinutes(5).isBefore(LocalDateTime.now());
    }

    private JobProcessor getJobProcessor(JobId jobId){
        return processors
                .stream()
                .filter(jobProcessor -> jobProcessor.getJobId().equals(jobId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format("JobProcessor not found to JobId = [%s]", jobId)));
    }

    private List<JobProcess> getJobProcesses(){
        var jobs = jobService.getAllActiveJobs();

        return jobService.getCurrentJobProcesses(jobs.stream().map(Job::getCurrentJobProcessId).toList());
    }

}
