package com.radarecom.radarecom.job.processor;

import com.radarecom.radarecom.entity.MLCategoryBatch;
import com.radarecom.radarecom.entity.MLJobProcess;
import com.radarecom.radarecom.job.service.MLJobService;
import com.radarecom.radarecom.repository.MLCategoryBatchRepository;
import com.radarecom.radarecom.search.util.ScraperUtil;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.radarecom.radarecom.constants.Constants.TRANSID;

@Service
@AllArgsConstructor
public class CategoryProcessor {

    private static final Logger log = LogManager.getLogger();

    private final MLCategoryBatchRepository mlCategoryBatchRepository;
    private final ScraperUtil scraperUtil;
    private final ProductBatchRepository productBatchRepository;
    private final MLJobService mlJobService;

    @Async("categoryExecutor")
    public void startWorker(MLJobProcess mlJobProcess) {
        ThreadContext.put(TRANSID, mlJobProcess.getTransId());

        try {
            while (true) {
                mlJobService.updateJobProcess(mlJobProcess.getMLJobId());

                var cutOff = LocalDateTime.now().minusMinutes(10);

                MLCategoryBatch category =
                        mlCategoryBatchRepository.lockNextCategoryToProcess(
                                cutOff,
                                LocalDateTime.now(),
                                LocalDateTime.now()
                        );

                if (category == null) {
                    break; // sai do loop, NÃO do método
                }

                try {
                    processCategory(category);
                    mlCategoryBatchRepository.markCompleted(
                            category.getId(),
                            LocalDateTime.now(),
                            LocalDateTime.now()
                    );
                } catch (Exception e) {
                    mlCategoryBatchRepository.markError(
                            category.getId(),
                            LocalDateTime.now(),
                            LocalDateTime.now()
                    );
                }
            }
        } finally {
            ThreadContext.clearAll(); // 🔥 GARANTIDO
        }
    }

    private void processCategory(MLCategoryBatch category) {

        var retries = category.getCurrentProcessRetries();

        log.info("Category [{}] | [{}] | Processing", category.getId(), category.getName());

        try {
            var searchItems = scraperUtil.getSearchPage(category.getUrl());

            if (searchItems.isEmpty()){

            }else {
                productBatchRepository.upsertBatch(searchItems);
                category.setCurrentPage(0);

                var totalPages = searchItems.getFirst().getTotalResults() > 2100 ? 42 : (searchItems.getFirst().getTotalResults() / 50 );
                category.setLastTotalPage(totalPages);

                mlCategoryBatchRepository.save(category);
            }

        }catch (Exception e){
            if (retries < 3){
                retries++;
                category.setCurrentProcessRetries(retries);
                mlCategoryBatchRepository.save(category);
                processCategory(category);
            }else {
                throw new RuntimeException();
            }
        }

        log.info("Category [{}] | [{}] | Processed", category.getId(), category.getName());

    }
}
