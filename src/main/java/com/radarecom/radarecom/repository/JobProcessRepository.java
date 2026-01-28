package com.radarecom.radarecom.repository;

import com.radarecom.radarecom.entity.JobProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface JobProcessRepository extends JpaRepository<JobProcess, Integer> {

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = """
            UPDATE JOB_PROCESS JP
            SET LAST_UPDATE = :lastUpdate
            FROM JOBS J
            WHERE JP.ID = J.CURRENT_JOB_PROCESS_ID
            AND J.ID = :jobId
            """, nativeQuery = true)
    void updateJobProcessByJobId(@Param("jobId") String jobId, @Param("lastUpdate") LocalDateTime lastUpdate);

}
