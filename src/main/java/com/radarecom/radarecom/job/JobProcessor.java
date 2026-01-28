package com.radarecom.radarecom.job;

import com.radarecom.radarecom.enums.JobId;

public interface JobProcessor {

    JobId getJobId();
    void execute(String originTransId);
    void close(String originTransId);

}
