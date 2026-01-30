package com.radarecom.radarecom.job.processor;

import com.radarecom.radarecom.entity.MLCategoryBatch;
import com.radarecom.radarecom.repository.MLCategoryBatchRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@AllArgsConstructor
public class CategoryProcessor {

    private final MLCategoryBatchRepository mlCategoryBatchRepository;

    @Async("categoryExecutor")
    public void startWorker(String originTransId) {

        while (true) {

            var cutOff = LocalDateTime.now().minusMinutes(10);

            MLCategoryBatch category =
                    mlCategoryBatchRepository.lockNextCategoryToProcess(cutOff, LocalDateTime.now(), LocalDateTime.now());

            if (category == null) return; // acabou trabalho

            try {
                processCategory(category);
                mlCategoryBatchRepository.markCompleted(category.getId(), LocalDateTime.now(), LocalDateTime.now());
            } catch (Exception e) {
                mlCategoryBatchRepository.markError(category.getId(), LocalDateTime.now(), LocalDateTime.now());
            }
        }
    }

    private void processCategory(MLCategoryBatch category) {

//        int page = category.getCurrentPage() != null
//                ? category.getCurrentPage()
//                : 1;
//
//        int totalPages = Integer.MAX_VALUE;
//
//        while (page <= totalPages) {
//
//            var result = scrapingService.scrapePage(
//                    category.getUrl(), page
//            );
//
//            totalPages = result.totalPages();
//
//            mlCategoryBatchRepository.updateProgress(
//                    category.getId(),
//                    page,
//                    totalPages
//            );
//
//            page++;

        System.out.println("Processando category = [ "+ category.getId() +" ]");
    }
}
