package com.radarecom.radarecom.controller;

import com.radarecom.radarecom.entity.MLJob;
import com.radarecom.radarecom.job.ScanMLCategoriesJob;
import com.radarecom.radarecom.job.scheduler.Scheduler;
import com.radarecom.radarecom.job.service.MLJobService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        //test.execute("abc");
        return ResponseEntity.ok("Alive");
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

}
