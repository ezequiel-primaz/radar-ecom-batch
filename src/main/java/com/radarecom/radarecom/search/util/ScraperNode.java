package com.radarecom.radarecom.search.util;

import com.radarecom.radarecom.search.integration.RadarEcomScraperRestTemplate;

import java.util.concurrent.Semaphore;

public class ScraperNode {

    private final RadarEcomScraperRestTemplate client;
    private final Semaphore semaphore = new Semaphore(30);

    public ScraperNode(RadarEcomScraperRestTemplate client) {
        this.client = client;
    }

    public RadarEcomScraperRestTemplate getClient() {
        return client;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }
}
