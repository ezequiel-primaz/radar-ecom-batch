package com.radarecom.radarecom.repository;

import com.radarecom.radarecom.entity.MLCategoryBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface MLCategoryBatchRepository extends JpaRepository<MLCategoryBatch, String> {

    @Transactional
    @Query(
            value = """
        WITH next_category AS (
            SELECT id, status
            FROM ML_CATEGORIES_BATCH
            WHERE
                (
                    STATUS = 'NOT_STARTED'
                    OR (
                        STATUS = 'IN_PROGRESS'
                        AND LAST_UPDATE < :inProgressCutoff
                    )
                )
            ORDER BY LAST_UPDATE NULLS FIRST
            LIMIT 1
            FOR UPDATE SKIP LOCKED
        )
        UPDATE ML_CATEGORIES_BATCH c
        SET
            STATUS = 'IN_PROGRESS',
            STARTED_AT = CASE
                WHEN c.STATUS = 'NOT_STARTED' THEN :startedAt
                ELSE c.STARTED_AT
            END,
            LAST_UPDATE = :lastUpdate
        FROM next_category nc
        WHERE c.ID = nc.ID
        RETURNING c.*
        """,
            nativeQuery = true
    )
    MLCategoryBatch lockNextCategoryToProcess(
            @Param("inProgressCutoff") LocalDateTime inProgressCutoff,
            @Param("startedAt") LocalDateTime startedAt,
            @Param("lastUpdate") LocalDateTime lastUpdate
    );

    @Modifying
    @Transactional
    @Query(
            value = """
        UPDATE ML_CATEGORIES_BATCH
        SET
            STATUS = 'COMPLETED',
            ENDED_AT = :endedAt,
            LAST_UPDATE = :lastUpdate
        WHERE ID = :id
        """,
            nativeQuery = true
    )
    void markCompleted(
            @Param("id") String id,
            @Param("endedAt") LocalDateTime endedAt,
            @Param("lastUpdate") LocalDateTime lastUpdate
    );

    @Modifying
    @Transactional
    @Query(
            value = """
        UPDATE ML_CATEGORIES_BATCH
        SET
            STATUS = 'ERROR',
            ENDED_AT = :endedAt,
            LAST_UPDATE = :lastUpdate
        WHERE ID = :id
        """,
            nativeQuery = true
    )
    void markError(
            @Param("id") String id,
            @Param("endedAt") LocalDateTime endedAt,
            @Param("lastUpdate") LocalDateTime lastUpdate
    );

    @Modifying
    @Transactional
    @Query(
            value = """
        UPDATE ML_CATEGORIES_BATCH
        SET
            CURRENT_PAGE = :currentPage,
            LAST_TOTAL_PAGE = :lastTotalPage,
            LAST_UPDATE = :lastUpdate
        WHERE ID = :id
        """,
            nativeQuery = true
    )
    void updateProgress(
            @Param("id") String id,
            @Param("currentPage") Integer currentPage,
            @Param("lastTotalPage") Integer lastTotalPage,
            @Param("lastUpdate") LocalDateTime lastUpdate
    );

    @Query(
            value = """
        SELECT COUNT(*) FROM ML_CATEGORIES_BATCH
        WHERE STATUS IN ('NOT_STARTED', 'IN_PROGRESS')
        """,
            nativeQuery = true
    )
    Long getCountByStatusNotStartedOrInProgress();

}
