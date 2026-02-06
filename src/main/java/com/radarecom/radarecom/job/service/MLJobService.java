package com.radarecom.radarecom.job.service;

import com.radarecom.radarecom.entity.MLJobProcess;
import com.radarecom.radarecom.entity.MLJob;
import com.radarecom.radarecom.entity.jsonb.MLJobProcessSummary;
import com.radarecom.radarecom.enums.MLJobId;
import com.radarecom.radarecom.enums.JobStatus;
import com.radarecom.radarecom.exception.NotFoundException;
import com.radarecom.radarecom.repository.MLJobProcessRepository;
import com.radarecom.radarecom.repository.MLJobRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class MLJobService {

    private static final Logger log = LogManager.getLogger();

    private final MLJobRepository mlJobRepository;
    private final MLJobProcessRepository mlJobProcessRepository;

    public List<MLJob> getAllJobs(){
        return mlJobRepository.findAll();
    }

    public List<MLJob> getAllActiveJobs(){
        return mlJobRepository.findAllByIsActive(true);
    }

    public MLJob getMLJobById(MLJobId mlJobId){
        return mlJobRepository.findById(mlJobId).orElseThrow(() -> new NotFoundException("MLJob " + mlJobId + " Not found!"));
    }

    public MLJobProcess getMLJobProcessById(Integer mlJobProcessId){
        return mlJobProcessRepository.findById(mlJobProcessId).orElseThrow(() -> new NotFoundException("MLJobProcess " + mlJobProcessId + " Not found!"));
    }

    public MLJobProcess createJobProcess(MLJobId mlJobId, String transId){
        var jobProcess = MLJobProcess
                .builder()
                .MLJobId(mlJobId)
                .status(JobStatus.NOT_STARTED)
                .transId(transId)
                .retries(0)
                .build();

        return mlJobProcessRepository.save(jobProcess);
    }

    @Transactional
    public void startMlJob(MLJob MLJob, Integer mlJobProcess){
        MLJob.setStatus(JobStatus.IN_PROGRESS);
        MLJob.setCurrentMLJobProcessId(mlJobProcess);
        mlJobRepository.save(MLJob);
    }

    @Transactional
    public void startMlJobProcess(MLJobProcess mlJobProcess){
        mlJobProcess.setStatus(JobStatus.IN_PROGRESS);
        mlJobProcess.setStartAt(LocalDateTime.now());
        mlJobProcessRepository.save(mlJobProcess);
    }

    public List<MLJobProcess> getCurrentMLJobProcesses(List<Integer> jobProcessesId){
        return mlJobProcessRepository.findAllById(jobProcessesId);
    }

    public boolean canStartJob(MLJob MLJob){
        return !MLJob.getStatus().equals(JobStatus.IN_PROGRESS);
    }

    public void updateJobProcess(MLJobId MLJobId){
        mlJobProcessRepository.updateJobProcessByJobId(MLJobId.toString(), LocalDateTime.now());
    }

    public boolean isJobAlreadyClosed(MLJobId MLJobId){
        var job = getMLJobById(MLJobId);

        return job.getStatus().equals(JobStatus.COMPLETED) || job.getStatus().equals(JobStatus.ERROR);
    }

    @Transactional
    public void closeJob(MLJobId MLJobId, JobStatus status, MLJobProcessSummary summary){
        try {
            var job = getMLJobById(MLJobId);
            var jobProcess = getMLJobProcessById(job.getCurrentMLJobProcessId());

            job.setStatus(status);

            jobProcess.setStatus(status);
            jobProcess.setEndAt(LocalDateTime.now());
            jobProcess.setSummary(summary);

            mlJobProcessRepository.save(jobProcess);
            mlJobRepository.save(job);
        }catch (Exception e) {
            log.error("Error closing Job = [{}] | message = [{}]", MLJobId, e.getMessage());
        }
    }

}
