package com.radarecom.radarecom.job;

import com.radarecom.radarecom.entity.MLJobProcess;
import com.radarecom.radarecom.enums.JobStatus;
import com.radarecom.radarecom.enums.MLJobId;

public interface Job {

    MLJobId getJobId();
    void start(MLJobProcess mlJobProcess);
    void execute(MLJobProcess mlJobProcess);
    void close(MLJobProcess mlJobProcess);
    void forceClose(MLJobProcess mlJobProcess);

}
