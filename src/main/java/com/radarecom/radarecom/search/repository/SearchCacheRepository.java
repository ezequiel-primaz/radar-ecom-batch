package com.radarecom.radarecom.search.repository;

import com.radarecom.radarecom.search.entity.SearchCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchCacheRepository extends JpaRepository<SearchCache, String> {

}
