package com.radarecom.radarecom.repository;

import com.radarecom.radarecom.entity.MLTokenSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MLTokenSystemRepository extends JpaRepository<MLTokenSystem, String> {
    Optional<MLTokenSystem> findByIdentity(String identity);

}
