package com.radarecom.radarecom.config;

import com.radarecom.radarecom.search.integration.RadarEcomScraperRestTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class ScraperConfig {

    @Bean
    @Qualifier("scraperExecutor")
    public Executor scraperExecutor() {
        return Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());
        // esse bean eh usado no ScraperUtil, para fazermos manualmente o gerenciamento de concorrencia com semaphore e executarmos com virtual threads.
    }

    @Bean
    public RadarEcomScraperRestTemplate scraperHostingerVps1() {
        return new RadarEcomScraperRestTemplate("http://72.60.246.35:8010/api/v2/scrapper/");
    }

    @Bean
    public RadarEcomScraperRestTemplate scraperHostingerVps2() {
        return new RadarEcomScraperRestTemplate("http://195.35.42.207:8010/api/v2/scrapper/");
    }

//    @Bean
//    public RadarEcomScraperRestTemplate local() {
//        return new RadarEcomScraperRestTemplate("http://localhost:8010/api/v2/scrapper/");
//    }
}
