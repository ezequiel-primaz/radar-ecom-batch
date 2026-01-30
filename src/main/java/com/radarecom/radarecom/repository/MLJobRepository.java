package com.radarecom.radarecom.repository;

import com.radarecom.radarecom.entity.MLJob;
import com.radarecom.radarecom.enums.MLJobId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MLJobRepository extends JpaRepository<MLJob, MLJobId> {

    List<MLJob> findAllByIsActive(boolean isActive);

}
