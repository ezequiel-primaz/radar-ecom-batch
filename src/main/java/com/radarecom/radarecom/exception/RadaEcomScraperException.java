package com.radarecom.radarecom.exception;

import org.springframework.web.client.RestClientResponseException;

public class RadaEcomScraperException extends RuntimeException{
    public RadaEcomScraperException(RestClientResponseException e){
        super("Error to call RadarEcom Scraper endpoint. Status: " + e.getStatusCode() + "Message: "  + e.getMessage());
    }
}
