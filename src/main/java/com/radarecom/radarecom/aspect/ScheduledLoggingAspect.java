package com.radarecom.radarecom.aspect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.radarecom.radarecom.constants.Constants.TRANSID;

@Aspect
@Component
public class ScheduledLoggingAspect {

    private static final Logger log = LogManager.getLogger();

    @Around("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public Object aroundScheduled(ProceedingJoinPoint pjp) throws Throwable {
        String transId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        ThreadContext.put(TRANSID, transId);

        try {
            startLog(pjp);

            Object result = pjp.proceed();
            return result;
        } finally {
            endLog(pjp, startTime);
            ThreadContext.clearAll();
        }
    }

    private void startLog(ProceedingJoinPoint pjp){
        ThreadContext.put("PTR", "START");
        log.info("Scheduler [{}] started", pjp.getSignature().getName());
        ThreadContext.remove("PTR");
    }

    private void endLog(ProceedingJoinPoint pjp, long startTime){
        long elapsed = System.currentTimeMillis() - startTime;
        ThreadContext.put("duration", String.valueOf(elapsed));
        ThreadContext.put("PTR", "END");
        log.info("Scheduler [{}] ended", pjp.getSignature().getName());
    }

}
