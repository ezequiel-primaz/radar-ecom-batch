package com.radarecom.radarecom.aspect;


import com.radarecom.radarecom.annotation.JobLogs;
import com.radarecom.radarecom.enums.JobId;
import com.radarecom.radarecom.job.JobProcessor;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static com.radarecom.radarecom.constants.Constants.*;

@Aspect
@Component
@AllArgsConstructor
public class JobLogsAspect {

    private static final Logger log = LogManager.getLogger();

    private final List<JobProcessor> processors;;

    @Around("@annotation(jobLogs)")
    public Object aroundJobLogs(ProceedingJoinPoint joinPoint, JobLogs jobLogs) throws Throwable {
        long startTime = System.currentTimeMillis();
        var shouldAddStartEndLogs = jobLogs.addStartEndLogs();

        var transId = getTransId(joinPoint, jobLogs);
        var originTransId = getOriginTransId(joinPoint, jobLogs);

        ThreadContext.put(TRANSID, transId);
        ThreadContext.put(ORIGIN_TRANSID, originTransId);

        var jobId = jobLogs.jobId();

        ThreadContext.put(JOB_ID, jobId.toString());


        try {
            startLog(jobId, shouldAddStartEndLogs);

            Object result = joinPoint.proceed();
            return result;
        } finally {
            endLog(jobId, startTime, shouldAddStartEndLogs);
            ThreadContext.clearAll();
        }
    }

    private void startLog(JobId jobId, boolean shouldAddStartEndLogs){
        if (shouldAddStartEndLogs){
            ThreadContext.put("PTR", "START");
            log.info("Job [{}] started", jobId.toString());
            ThreadContext.remove("PTR");
        }
    }

    private void endLog(JobId jobId, long startTime, boolean shouldAddStartEndLogs){
        long elapsed = System.currentTimeMillis() - startTime;
        ThreadContext.put("duration", String.valueOf(elapsed));
        if (shouldAddStartEndLogs){
            ThreadContext.put("PTR", "END");
            log.info("Job [{}] ended", jobId.toString());
        }
    }

    private String getTransId(ProceedingJoinPoint joinPoint, JobLogs jobLogs){
        var argNumber = jobLogs.transIdArgNumber();
        if (argNumber > 0){
            Object[] args = joinPoint.getArgs();

            Object transIdArg = args.length > (argNumber-1) ? args[argNumber-1] : null;

            return transIdArg.toString();
        }else {
            return UUID.randomUUID().toString();
        }
    }

    private String getOriginTransId(ProceedingJoinPoint joinPoint, JobLogs jobLogs){
        var argNumber = jobLogs.originTransIdArgNumber();
        if (argNumber > 0){
            Object[] args = joinPoint.getArgs();

            Object originTransIdArg = args.length > (argNumber-1) ? args[argNumber-1] : null;

            return originTransIdArg.toString();
        }
        return null;
    }

}
