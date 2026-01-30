package com.radarecom.radarecom.repository;

import com.radarecom.radarecom.entity.MLJobProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface MLJobProcessRepository extends JpaRepository<MLJobProcess, Integer> {

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(value = """
            UPDATE ML_JOB_PROCESS JP
            SET LAST_UPDATE = :lastUpdate
            FROM ML_JOBS J
            WHERE JP.ID = J.CURRENT_ML_JOB_PROCESS_ID
            AND J.ID = :MLJobId
            """, nativeQuery = true)
    void updateJobProcessByJobId(@Param("MLJobId") String MLJobId, @Param("lastUpdate") LocalDateTime lastUpdate);

}
