package com.radarecom.radarecom.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.radarecom.radarecom.entity.MLTokenSystem;
import com.radarecom.radarecom.exception.*;
import com.radarecom.radarecom.integration.request.MLOauthTokenRequest;
import com.radarecom.radarecom.integration.response.*;
import com.radarecom.radarecom.repository.MLTokenSystemRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

public class MercadoLivreIntegration {

    private static final String TOKEN_IDENTITY = "ML-TOKEN-IDENTITY";

    private static String URL = "https://api.mercadolibre.com/";
    private static String OAUTH_URL = "https://api.mercadolibre.com/oauth/token";

    private static String TOKEN;
    private static String REFRESH_TOKEN;

    private static String CLIENT_ID;
    private static String CLIENT_SECRET;
    private static String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
    private static String GRANT_TYPE_AUTHORIZATION_CODE= "authorization_code";

    RestTemplate restTemplate = new RestTemplate();
    ObjectMapper objectMapper = new ObjectMapper();

    private MLTokenSystemRepository mlTokenSystemRepository;

    public MercadoLivreIntegration(String clientId, String clientSecret, MLTokenSystemRepository mlTokenSystemRepository){
        this.mlTokenSystemRepository = mlTokenSystemRepository;
        getTokenDataFromMlTokenSystem();
        CLIENT_ID = clientId;
        CLIENT_SECRET = clientSecret;
        refreshToken();
    }

    private void getTokenDataFromMlTokenSystem(){
        var mlTokenSystem = mlTokenSystemRepository.findByIdentity(TOKEN_IDENTITY);
        if (mlTokenSystem.isPresent()){
            TOKEN = mlTokenSystem.get().getToken();
            REFRESH_TOKEN = mlTokenSystem.get().getRefreshToken();
        }
    }

    public void refreshToken(){
        try{
            var mlOauthTokenRequest = MLOauthTokenRequest.builder()
                    .grant_type(GRANT_TYPE_REFRESH_TOKEN)
                    .client_id(CLIENT_ID)
                    .client_secret(CLIENT_SECRET)
                    .refresh_token(REFRESH_TOKEN)
                    .build();

            MercadoLivreOauthToken mercadoLivreOauthToken = restTemplate.postForObject(OAUTH_URL, mlOauthTokenRequest, MercadoLivreOauthToken.class);

            if (mercadoLivreOauthToken != null){
                TOKEN = mercadoLivreOauthToken.getAccessToken();
                REFRESH_TOKEN = mercadoLivreOauthToken.getRefreshToken();
                updateMlTokenSystem();
            }
        }catch (Exception e){
            throw new UnexpectedErrorException("Error with integration, please contact the support.");
        }
    }

    private void updateMlTokenSystem(){
        var mlTokenSystem = mlTokenSystemRepository.findByIdentity(TOKEN_IDENTITY);
        if (mlTokenSystem.isPresent()){
            mlTokenSystem.get().setToken(TOKEN);
            mlTokenSystem.get().setRefreshToken(REFRESH_TOKEN);
            mlTokenSystemRepository.save(mlTokenSystem.get());
        }else{
            var newMlTokenSystem = new MLTokenSystem();
            newMlTokenSystem.setIdentity(TOKEN_IDENTITY);
            newMlTokenSystem.setToken(TOKEN);
            newMlTokenSystem.setRefreshToken(REFRESH_TOKEN);
            mlTokenSystemRepository.save(newMlTokenSystem);
        }
    }

    public void refreshTokenByCode(String code){

        var mlOauthTokenRequest = MLOauthTokenRequest.builder()
                .grant_type(GRANT_TYPE_AUTHORIZATION_CODE)
                .client_id(CLIENT_ID)
                .client_secret(CLIENT_SECRET)
                .redirect_uri("https://radar-ecom-c185187fc4b2.herokuapp.com/mercadolivre/refresh-token-by-code")
                .code(code)
                .build();

        MercadoLivreOauthToken mercadoLivreOauthToken = restTemplate.postForObject(OAUTH_URL, mlOauthTokenRequest, MercadoLivreOauthToken.class);

        if (mercadoLivreOauthToken != null){
            TOKEN = mercadoLivreOauthToken.getAccessToken();
            REFRESH_TOKEN = mercadoLivreOauthToken.getRefreshToken();
            updateMlTokenSystem();
        }
    }

    @Retryable(backoff = @Backoff(delay = 1), maxAttempts = 2)
    public ResponseEntity<CatalogML> getCatalogById(String catalogId) {
        var url = URL + "products/" + catalogId;

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("include_attributes","all").build();

        try {
            ResponseEntity<CatalogML> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, buildHeaders(), CatalogML.class);

            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        }catch (HttpClientErrorException.Unauthorized e) {
            refreshToken();
            throw new MLIntegrationException(e.getMessage());
        } catch (HttpClientErrorException e) {
            throw new MLIntegrationException(e.getMessage());
        }
    }

    @Retryable(backoff = @Backoff(delay = 1, multiplier = 2))
    public ResponseEntity<BodyItemML> getItemById(String itemId) {
        var url = URL + "items";

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("ids",itemId)
                .queryParam("include_attributes","all").build();

        try {
            ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, buildHeaders(), String.class);

            ItemML[] itemML = objectMapper.readValue(response.getBody(), ItemML[].class);

            if ( itemML[0].getCode() != 200 ) throw new MLItemNotFound();

            return new ResponseEntity<>(itemML[0].getBody(), response.getStatusCode());
        }catch (MLItemNotFound e){
            refreshToken();
            throw new MLItemNotFound();
        } catch (Exception e){
            refreshToken();
            throw new UnexpectedErrorException(e.getMessage());
        }
    }

    @Retryable(backoff = @Backoff(delay = 1, multiplier = 2))
    public ResponseEntity<Long> getItemVisitCountById(String itemId) {
        var url = URL + "visits/items";

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("ids",itemId).build();

        try {
            ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, buildHeaders(), String.class);

            var visits = Long.parseLong(response.getBody().split("\":")[1].split("}")[0]);

            return new ResponseEntity<>(visits, response.getStatusCode());
        }catch (HttpClientErrorException.Unauthorized e) {
            refreshToken();
            throw new MLIntegrationException(e.getMessage());
        } catch (HttpClientErrorException e) {
            throw new MLIntegrationException(e.getMessage());
        }
    }

    @Retryable(backoff = @Backoff(delay = 1, multiplier = 2))
    public ResponseEntity<ItemVisitsML> getVisitsByTimeframeInDays(String itemId, Integer timeframeInDays) {
        var url = URL + "items/"+itemId+"/visits/time_window";

        var currentDate = LocalDate.now();

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("last", timeframeInDays.toString())
                .queryParam("unit", "day")
                .queryParam("ending", currentDate.toString())
                .build();

        try {
            ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, buildHeaders(), String.class);
            if ( !response.getStatusCode().equals(HttpStatus.OK) ) throw new MLItemNotFound();

            ItemVisitsML itemVisits = objectMapper.readValue(response.getBody(), ItemVisitsML.class);

            return new ResponseEntity<>(itemVisits, response.getStatusCode());
        }catch (MLItemNotFound e){
            refreshToken();
            throw new MLItemNotFound();
        }catch (Exception e){
            refreshToken();
            throw new UnexpectedErrorException(e.getMessage());
        }
    }

    public ResponseEntity<MLType> getTypeDetailByTypeId(String typeId) {
        var url = URL + "sites/MLB/listing_types/" + typeId;

        try {
            ResponseEntity<MLType> response = restTemplate.exchange(url, HttpMethod.GET, buildHeaders(), MLType.class);
            if ( !response.getStatusCode().equals(HttpStatus.OK)) throw new MLItemNotFound();

            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        }catch (MLItemNotFound e){
            throw new MLItemNotFound();
        }catch (Exception e){
            refreshToken();
            throw new UnexpectedErrorException(e.getMessage());
        }
    }

    @Retryable(backoff = @Backoff(delay = 1), maxAttempts = 2)
    public List<MLCategoryResponse> getCategories() {
        var url = URL + "sites/MLB/categories";

        try {
            ResponseEntity<List<MLCategoryResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    buildHeaders(),
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody();
        } catch (HttpClientErrorException.Unauthorized e) {
            refreshToken();
            throw new MLIntegrationException(e.getMessage());
        } catch (HttpClientErrorException e) {
            throw new MLIntegrationException(e.getMessage());
        }
    }

    @Retryable(backoff = @Backoff(delay = 1), maxAttempts = 2)
    public MLCategoryResponse getCategoryById(String categoryId) {
        var url = URL + "categories/" + categoryId;

        try {
            var response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    buildHeaders(),
                    MLCategoryResponse.class
            );
            return response.getBody();
        } catch (HttpClientErrorException.Unauthorized e) {
            refreshToken();
            throw new MLIntegrationException(e.getMessage());
        } catch (HttpClientErrorException e) {
            throw new MLIntegrationException(e.getMessage());
        }
    }

    @Retryable(backoff = @Backoff(delay = 1), maxAttempts = 2)
    public MLUserResponse getSellerInfo(String sellerId) {
        var url = URL + "users/" + sellerId;

        try {
            var response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    buildHeaders(),
                    MLUserResponse.class
            );
            return response.getBody();
        } catch (HttpClientErrorException.Unauthorized e) {
            refreshToken();
            throw new MLIntegrationException(e.getMessage());
        } catch (HttpClientErrorException e) {
            throw new MLIntegrationException(e.getMessage());
        }
    }

    private HttpEntity<Void> buildHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + TOKEN);
        return new HttpEntity<>(headers);
    }



}
