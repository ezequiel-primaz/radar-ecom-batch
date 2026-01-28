package com.radarecom.radarecom.search.service;

import com.radarecom.radarecom.search.entity.Product;
import com.radarecom.radarecom.search.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Optional<Product> getProductById(String id){
        return productRepository.findById(id);
    }

    public Product saveProduct(Product product){
        return productRepository.save(product);
    }

    public List<Product> getProductsByIds(List<String> ids){
        return productRepository.findAllById(ids);
    }

}
