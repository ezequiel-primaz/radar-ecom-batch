package com.radarecom.radarecom.repository;

import com.radarecom.radarecom.entity.Job;
import com.radarecom.radarecom.enums.JobId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, JobId> {

    List<Job> findAllByIsActive(boolean isActive);

}
