package com.radarecom.radarecom.config;

import com.radarecom.radarecom.integration.MercadoLivreIntegration;
import com.radarecom.radarecom.repository.MLTokenSystemRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executors;

@Configuration
@EnableRetry
@EnableScheduling
@EnableAsync
public class RadarEcomConfig {

    @Bean
    @Qualifier("taskExecutor")
    public TaskExecutor taskExecutor() {
        return new TaskExecutorAdapter(
                Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory())
        );
        // esse bean eh usado para os schedulers saberem qual taskExecutor usar e usar com virtual threads.
    }

    @Bean
    public MercadoLivreIntegration mercadoLivreIntegration(@Value("${clientId}")String clientId, @Value("${clientSecret}")String clientSecret,
                                                           MLTokenSystemRepository mlTokenSystemRepository){
        return new MercadoLivreIntegration(clientId, clientSecret, mlTokenSystemRepository);
    }

}
