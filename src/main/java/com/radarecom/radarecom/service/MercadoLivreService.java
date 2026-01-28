package com.radarecom.radarecom.service;

import com.radarecom.radarecom.integration.MercadoLivreIntegration;
import com.radarecom.radarecom.integration.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MercadoLivreService {

    private static final Double TAX = 0.07;

    private final MercadoLivreIntegration mercadoLivreIntegration;

    public CatalogML getCatalogById(String catalogId){
        return mercadoLivreIntegration.getCatalogById(catalogId).getBody();
    }

    public Long getTotalVisitsById(String itemId){
        return mercadoLivreIntegration.getItemVisitCountById(itemId).getBody();
    }

    public ItemVisitsML getVisitsByTimeframeInDays(String itemId, Integer timeframeInDays){
        return mercadoLivreIntegration.getVisitsByTimeframeInDays(itemId, timeframeInDays).getBody();
    }

    public MLUserResponse getSellerInfo(String sellerId){
        return mercadoLivreIntegration.getSellerInfo(sellerId);
    }

}
