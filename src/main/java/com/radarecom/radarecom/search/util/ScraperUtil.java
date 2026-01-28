package com.radarecom.radarecom.search.util;

import com.radarecom.radarecom.search.dto.SearchItem;
import com.radarecom.radarecom.search.dto.melidata.MelidataResponse;
import com.radarecom.radarecom.search.integration.RadarEcomScraperRestTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ScraperUtil {

    private final Executor scraperExecutor;
    private final List<ScraperNode> nodes;
    private final AtomicInteger roundRobin = new AtomicInteger(0);

    //TODO Urgente:
    //TODO Temos q implementar um semaphoro ou algo do tipo aqui, pois quem esta controlando a concorrencia eh a chamada no SearchService...
    //TODO a classe ScraperUtil tem q saber o limite de chamada q ela pode receber simultanemanete... para entao dividir entre os containers de scrapping
    public ScraperUtil(
            @Qualifier("scraperExecutor") Executor scraperExecutor,
            List<RadarEcomScraperRestTemplate> scraperClients // várias instâncias
    ) {
        this.scraperExecutor = scraperExecutor;
        this.nodes = scraperClients.stream()
                .map(ScraperNode::new)
                .toList();
    }

    private ScraperNode pickNode() {
        int idx = Math.abs(roundRobin.getAndIncrement() % nodes.size());
        return nodes.get(idx);
    }

    public MelidataResponse getProductDetail(String url) {

        ScraperNode node = pickNode();
        Semaphore sem = node.getSemaphore();

        try {
            sem.acquire();

            return CompletableFuture.supplyAsync(
                    () -> node.getClient().fetchProductDetail(url),
                    scraperExecutor
            ).join();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for scraper slot", e);

        } finally {
            sem.release();
        }
    }

    public List<SearchItem> getSearchPage(String url) {

        ScraperNode node = pickNode();
        Semaphore sem = node.getSemaphore();

        try {
            sem.acquire();

            return CompletableFuture.supplyAsync(
                    () -> node.getClient().fetchSearchPage(url),
                    scraperExecutor
            ).join();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for scraper slot", e);

        } finally {
            sem.release();
        }
    }

}