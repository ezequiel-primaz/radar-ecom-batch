package com.radarecom.radarecom.job.scheduler;

import com.radarecom.radarecom.entity.MLJob;
import com.radarecom.radarecom.entity.MLJobProcess;
import com.radarecom.radarecom.enums.MLJobId;
import com.radarecom.radarecom.exception.NotFoundException;
import com.radarecom.radarecom.job.Job;
import com.radarecom.radarecom.job.service.MLJobService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static com.radarecom.radarecom.enums.JobStatus.*;

@Component
@AllArgsConstructor
public class Scheduler {

    private static final Logger log = LogManager.getLogger();

    private final List<Job> processors;

    private final MLJobService mlJobService;

    @Async
    @Scheduled(cron = "0 10 0 * * *", zone = "America/Sao_Paulo") // roda 1 vez as 00:10
    //@Scheduled(fixedRate = 600000000) || ao executar a app.
    public void startJobs() {
        log.info("Starting MLJobs");

        var mlJobs = mlJobService.getAllActiveJobs();

        mlJobs.forEach(mlJob -> {

            if (!mlJob.getStatus().equals(NOT_STARTED)) {
                log.error("MLJob [{}] cannot be started. Current STATUS = [{}]", mlJob.getId(), mlJob.getStatus());
            }else {
                String transId = UUID.randomUUID().toString();
                var mlJobProcess = mlJobService.createJobProcess(mlJob.getId(), transId);
                mlJobService.startMlJob(mlJob, mlJobProcess.getId());
                getJobProcessor(mlJob.getId()).start(mlJobProcess);

                log.info("MLJob [{}] Started. MLJobProcess = [{}]", mlJob.getId(), mlJobProcess.getId());
            }
        });

        handleJobs();
    }

    @Async
    @Scheduled(fixedRate = 15 * 60 * 1000, initialDelay = 15 * 60 * 1000) // roda a cada 15 min || espera 15 min pra comecar ao startar
    //@Scheduled(initialDelay = 120000, fixedRate = 120000) || roda de 2 em 2 min
    public void handleJobs() {

        var mlJobProcesses = getJobProcesses();

        mlJobProcesses.forEach(mlJobProcess -> {
            getJobProcessor(mlJobProcess.getMLJobId()).execute(mlJobProcess);
        });
    }

    @Async
    @Scheduled(fixedRate = 15 * 60 * 1000, initialDelay = 30 * 60 * 1000) // roda a cada 15 min || espera 30 min pra comecar ao startar
    //@Scheduled(initialDelay = 600000) || espera 10 min pra rodar ao startar
    public void closeJobs() {

        var mlJobProcesses = getJobProcesses();

        mlJobProcesses.forEach(mlJobProcess -> {
            getJobProcessor(mlJobProcess.getMLJobId()).close(mlJobProcess);
        });
    }

    @Async
    @Scheduled(cron = "0 0 23 * * *", zone = "America/Sao_Paulo") // roda 1 vez as 23:00
    //@Scheduled(initialDelay = 600000) || espera 10 min pra rodar ao startar
    public void forceCloseJobs() {

        log.info("Force Close remaining MLJobs.");

        var mlJobProcesses = getJobProcesses();

        mlJobProcesses.forEach(mlJobProcess -> {
            getJobProcessor(mlJobProcess.getMLJobId()).forceClose(mlJobProcess);
        });
    }

    private List<MLJobProcess> getJobProcesses(){
        var jobs = mlJobService.getAllActiveJobs();

        return mlJobService.getCurrentMLJobProcesses(jobs.stream().map(MLJob::getCurrentMLJobProcessId).toList());
    }


    private Job getJobProcessor(MLJobId MLJobId){
        return processors
                .stream()
                .filter(job -> job.getJobId().equals(MLJobId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format("JobProcessor not found to JobId = [%s]", MLJobId)));
    }

}
