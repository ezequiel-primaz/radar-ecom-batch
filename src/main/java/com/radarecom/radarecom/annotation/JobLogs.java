package com.radarecom.radarecom.annotation;

import com.radarecom.radarecom.enums.JobId;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JobLogs {

    JobId jobId();
    boolean addStartEndLogs() default false;
    int transIdArgNumber() default -1;
    int originTransIdArgNumber() default -1;

}
