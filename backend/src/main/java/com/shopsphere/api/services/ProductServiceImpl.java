package com.shopsphere.api.services;

import com.shopsphere.api.entity.Product;
import com.shopsphere.api.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Product updateStock(String productId, Integer stockLevel) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStockLevel(stockLevel);
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateThreshold(String productId, Integer threshold) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setReorderThreshold(threshold);
        return productRepository.save(product);
    }
}
