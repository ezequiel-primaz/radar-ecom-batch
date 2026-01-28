package com.radarecom.radarecom.job.service;

import com.radarecom.radarecom.enums.JobId;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import static com.radarecom.radarecom.enums.JobId.TRACKING_JOB;

@Service
@AllArgsConstructor
public class TrackingJobService {

    private static final Logger log = LogManager.getLogger();

    private final static JobId JOB_ID = TRACKING_JOB;


}
