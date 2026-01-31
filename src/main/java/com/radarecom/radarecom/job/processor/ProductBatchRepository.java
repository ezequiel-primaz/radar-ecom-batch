package com.radarecom.radarecom.job.processor;

import com.radarecom.radarecom.search.dto.SearchItem;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductBatchRepository {

    private final JdbcTemplate jdbcTemplate;

    public void upsertBatch(List<SearchItem> items) {

        String sql = """
            INSERT INTO products (
                id,
                url,
                type,
                name,
                pic_url,
                price,
                sales,
                radar_status,
                ml_status,
                radar_created_at,
                radar_last_update,
                provider
            ) VALUES (
                ?, ?, ?, ?, ?, ?, ?,
                'PENDING',
                'ACTIVE',
                now(),
                now(),
                'MERCADO_LIVRE'
            )
            ON CONFLICT (id)
            DO UPDATE SET
                url = EXCLUDED.url,
                name = EXCLUDED.name,
                pic_url = EXCLUDED.pic_url,
                price = EXCLUDED.price,
                sales = EXCLUDED.sales,
                ml_status = 'ACTIVE',
                radar_last_update = now()
        """;

        jdbcTemplate.batchUpdate(
                sql,
                items,
                50,
                (ps, item) -> {
                    ps.setString(1, item.getId());
                    ps.setString(2, item.getUrl());
                    ps.setString(3, item.getType().toString());
                    ps.setString(4, item.getName());
                    ps.setString(5, item.getImageUrl());
                    ps.setDouble(6, item.getPrice());
                    ps.setInt(7, item.getSales());
                }
        );
    }
}


