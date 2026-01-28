package com.radarecom.radarecom.search.integration;

import com.radarecom.radarecom.exception.RadaEcomScraperException;
import com.radarecom.radarecom.search.dto.SearchItem;
import com.radarecom.radarecom.search.dto.melidata.MelidataResponse;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.nonNull;

public class RadarEcomScraperRestTemplate {

    private String SCRAPER_API_URL;
    private final RestTemplate restTemplate;

    public RadarEcomScraperRestTemplate(String url){
        this.restTemplate = new RestTemplate();
        SCRAPER_API_URL = url;
    }

    public MelidataResponse fetchProductDetail(String targetUrl) {
        String url = SCRAPER_API_URL + "product-details" +"?url=" + targetUrl;
        return execute(url, HttpMethod.GET, null, MelidataResponse.class);
    }

    @Retryable(backoff = @Backoff(delay = 1), maxAttempts = 2)
    public List<SearchItem> fetchSearchPage(String targetUrl) {
        String url = SCRAPER_API_URL + "search-page" + "?url=" + targetUrl;
        SearchItem[] array = execute(url, HttpMethod.GET, null, SearchItem[].class);
        return Arrays.asList(array);
    }

    private <T> T execute(String url, HttpMethod method, Object body, Class<T> responseType) {
        try {
            ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    method,
                    buildHeaders(body),
                    responseType
            );
            return response.getBody();
        } catch (RestClientResponseException e) {
            throw new RadaEcomScraperException(e);
        }
    }

    private HttpEntity<Object> buildHeaders(Object object){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return nonNull(object) ? new HttpEntity<>(object, headers) : new HttpEntity<>(headers);
    }

}
