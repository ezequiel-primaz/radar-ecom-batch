package com.radarecom.radarecom.job.scheduler;

import com.radarecom.radarecom.entity.MLJob;
import com.radarecom.radarecom.entity.MLJobProcess;
import com.radarecom.radarecom.enums.MLBatchSchedulerIdAction;
import com.radarecom.radarecom.enums.MLJobId;
import com.radarecom.radarecom.exception.NotFoundException;
import com.radarecom.radarecom.job.Job;
import com.radarecom.radarecom.job.service.MLJobService;
import com.radarecom.radarecom.repository.MLBatchSchedulerRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.radarecom.radarecom.enums.JobStatus.*;
import static com.radarecom.radarecom.enums.MLBatchSchedulerIdAction.START;

@Component
@AllArgsConstructor
public class Scheduler {

    private static final Logger log = LogManager.getLogger();

    private final List<Job> processors;

    private final MLJobService mlJobService;
    private final MLBatchSchedulerRepository mlBatchSchedulerRepository;

    @Async
    @Scheduled(cron = "0 10-59/10 0 * * *", zone = "America/Sao_Paulo") // comeca rodar as 00:10 e dps de 10 em 10 ate 00:50.
    @Scheduled(cron = "0 0-30/10 1 * * *", zone = "America/Sao_Paulo") // comeca rodar as 01:00 e dps de 10 em 10 ate 1:30.
    public void startJobs() {
        if (shouldExecuteSchedulerAction(START)){
            log.info("Starting MLJobs");

            var mlJobs = mlJobService.getAllActiveJobs();

            mlJobs.forEach(mlJob -> {

                if (mlJob.getStatus().equals(IN_PROGRESS)) {
                    log.error("MLJob [{}] cannot be started. Current STATUS = [{}]", mlJob.getId(), mlJob.getStatus());
                }else {
                    String transId = UUID.randomUUID().toString();
                    var mlJobProcess = mlJobService.createJobProcess(mlJob.getId(), transId);
                    mlJobService.startMlJob(mlJob, mlJobProcess.getId());
                    getJobProcessor(mlJob.getId()).start(mlJobProcess);

                    log.info("MLJob [{}] Started. MLJobProcess = [{}]", mlJob.getId(), mlJobProcess.getId());
                }
            });

            updateSchedulerAction(START);

            handleJobs();
        }
    }

    @Async
    @Scheduled(fixedRate = 15 * 60 * 1000, initialDelay = 15 * 60 * 1000) // roda a cada 15 min || espera 15 min pra comecar ao startar
    public void handleJobs() {
        // as proprias classes de Job com o metodo execute sao responsaveis por saber se ja esta rodando e se devem comecar o processamento dnv.
        var mlJobProcesses = getJobProcesses();

        mlJobProcesses.forEach(mlJobProcess -> {
            getJobProcessor(mlJobProcess.getMLJobId()).execute(mlJobProcess);
        });
    }

    @Async
    @Scheduled(fixedRate = 15 * 60 * 1000, initialDelay = 30 * 60 * 1000) // roda a cada 15 min || espera 30 min pra comecar ao startar
    public void closeJobs() {
        // as proprias classes de Job com o metodo close sao responsaveis por saber se ja esta rodando e se devem comecar o processamento dnv.
        var mlJobProcesses = getJobProcesses();

        mlJobProcesses.forEach(mlJobProcess -> {
            getJobProcessor(mlJobProcess.getMLJobId()).close(mlJobProcess);
        });
    }

    @Async
    @Scheduled(cron = "0 0-50/10 23 * * *", zone = "America/Sao_Paulo") // comeca rodar as 23:00 e dps de 10 em 10 ate 23:50.
    public void forceCloseJobs() {
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

    private boolean shouldExecuteSchedulerAction(MLBatchSchedulerIdAction actionId){
        var currentDate = LocalDate.now();
        var action = mlBatchSchedulerRepository.findById(actionId).orElseThrow();
        return action.getLastUpdate().isBefore(currentDate);
    }

    private void updateSchedulerAction(MLBatchSchedulerIdAction actionId){
        var action = mlBatchSchedulerRepository.findById(actionId).orElseThrow();
        action.setLastUpdate(LocalDate.now());
        mlBatchSchedulerRepository.save(action);
    }

}
