package com.radarecom.radarecom.controller;

import com.radarecom.radarecom.entity.MLJob;
import com.radarecom.radarecom.entity.MLJobProcess;
import com.radarecom.radarecom.job.scheduler.Scheduler;
import com.radarecom.radarecom.job.service.MLJobService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/batch")
@AllArgsConstructor
public class BatchController {

    private final MLJobService mlJobService;
    private final Scheduler scheduler;

    @GetMapping("/health-check")
    public ResponseEntity<String> healthCheck(){
        return ResponseEntity.ok("Alive");
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<MLJob>> getJobs(){
        var jobs = mlJobService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/job-process/{id}")
    public ResponseEntity<MLJobProcess> getJobProcess(@PathVariable Integer id){
        var jobs = mlJobService.getMLJobProcessById(id);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/start-jobs")
    public ResponseEntity<String> startJobs(){
        scheduler.startJobs();
        return ResponseEntity.ok("Started");
    }

    @GetMapping("/execute-jobs")
    public ResponseEntity<String> executeJobs(){
        scheduler.handleJobs();
        return ResponseEntity.ok("Executed");
    }

    @GetMapping("/close-jobs")
    public ResponseEntity<String> closeJobs(){
        scheduler.closeJobs();
        return ResponseEntity.ok("Closed");
    }

    @GetMapping("/force-close-jobs")
    public ResponseEntity<String> forceCloseJobs(){
        scheduler.forceCloseJobs();
        return ResponseEntity.ok("Force Closed");
    }

}
