package com.radarecom.radarecom.repository;

import com.radarecom.radarecom.entity.MLBatchSchedulerAction;
import com.radarecom.radarecom.enums.MLBatchSchedulerIdAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MLBatchSchedulerRepository extends JpaRepository<MLBatchSchedulerAction, MLBatchSchedulerIdAction> {
}
