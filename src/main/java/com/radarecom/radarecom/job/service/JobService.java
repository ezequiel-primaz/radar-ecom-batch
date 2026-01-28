package com.radarecom.radarecom.job.service;

import com.radarecom.radarecom.entity.JobProcess;
import com.radarecom.radarecom.entity.Job;
import com.radarecom.radarecom.enums.JobId;
import com.radarecom.radarecom.enums.JobStatus;
import com.radarecom.radarecom.exception.NotFoundException;
import com.radarecom.radarecom.repository.JobProcessRepository;
import com.radarecom.radarecom.repository.JobRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.radarecom.radarecom.enums.JobStatus.*;

@Service
@AllArgsConstructor
public class JobService {

    private static final Logger log = LogManager.getLogger();

    private final JobRepository jobRepository;
    private final JobProcessRepository jobProcessRepository;

    public List<Job> getAllActiveJobs(){
        return jobRepository.findAllByIsActive(true);
    }

    public JobProcess createJobProcess(JobId jobId, String transId){
        var jobProcess = JobProcess
                .builder()
                .jobId(jobId)
                .status(JobStatus.IN_PROGRESS)
                .startAt(LocalDateTime.now())
                .transId(transId)
                .build();

        return jobProcessRepository.save(jobProcess);
    }

    public void startJob(Job job, Integer currentJobId){
        job.setStatus(JobStatus.IN_PROGRESS);
        job.setCurrentJobProcessId(currentJobId);
        job.setLastUpdate(LocalDateTime.now());
        jobRepository.save(job);
    }

    public boolean canStartJob(Job job){
        return !job.getStatus().equals(JobStatus.IN_PROGRESS);
    }

    public List<JobProcess> getCurrentJobProcesses(List<Integer> jobProcessesId){
        return jobProcessRepository.findAllById(jobProcessesId);
    }

    public void updateJobProcess(JobId jobId){
        jobProcessRepository.updateJobProcessByJobId(jobId.toString(), LocalDateTime.now());
    }

    public boolean isJobAlreadyClosed(JobId jobId){
        var job = getJobById(jobId);

        return job.getStatus().equals(JobStatus.COMPLETED) || job.getStatus().equals(JobStatus.ERROR);
    }

    @Transactional
    public JobStatus closeJob(JobId jobId){
//        JobStatus status = ERROR;
//
//        var count = userJobService.getCountByStatus(jobId, List.of(IN_PROGRESS.name(), NOT_STARTED.name(), ERROR.name()));
//
//        if (count > 0){
//            userJobService.setAllToNotStarted(jobId);
//            closeJob(jobId, ERROR);
//        }else{
//            userJobService.setAllToNotStarted(jobId);
//            closeJob(jobId, JobStatus.COMPLETED);
//            status = COMPLETED;
//        }
//        return status;
        return null;
    }

    private void closeJob(JobId jobId, JobStatus status){
        try {
            var job = getJobById(jobId);
            var jobProcess = getJobProcessById(job.getCurrentJobProcessId());

            job.setStatus(status);
            job.setLastUpdate(LocalDateTime.now());

            jobProcess.setStatus(status);
            jobProcess.setLastUpdate(LocalDateTime.now());
            jobProcess.setEndAt(LocalDateTime.now());

            jobProcessRepository.save(jobProcess);
            jobRepository.save(job);
        }catch (Exception e) {
            log.error("Error closing Job = [{}] | message = [{}]", jobId, e.getMessage());
        }
    }

    private Job getJobById(JobId jobId){
        return jobRepository.findById(jobId).orElseThrow(() -> new NotFoundException(String.format("Job not found! [%s]", jobId)));
    }

    private JobProcess getJobProcessById(Integer jobProcessId){
        return jobProcessRepository.findById(jobProcessId).orElseThrow(() -> new NotFoundException(String.format("JobProcess not found! [%s]", jobProcessId)));
    }

}
