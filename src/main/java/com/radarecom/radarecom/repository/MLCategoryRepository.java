package com.radarecom.radarecom.repository;

import com.radarecom.radarecom.entity.MLCategory;
import com.radarecom.radarecom.entity.MLCategoryID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MLCategoryRepository extends JpaRepository<MLCategory, MLCategoryID> {

    List<MLCategory> findAllByLevel(Integer level);
    List<MLCategory> findAllByIdParentId(String parentId);
    Optional<MLCategory> findByIdId(String id);

}
