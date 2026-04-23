package org.example.controllers;

import org.example.models.Product;
import org.example.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 1. Добавяне на НОВ продукт (POST)
    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }

    // 2. Изтриване на продукт по ID (DELETE)
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
    }

    // 3. Обновяване на рейтинга на продукт (PUT)
    @PutMapping("/{id}/rating")
    public ResponseEntity<?> updateProductRating(@PathVariable Long id, @RequestBody Map<String, Double> payload) {
        // Взимаме новото число от JSON обекта, изпратен от Vue
        Double newRating = payload.get("rating");

        if (newRating == null) {
            return ResponseEntity.badRequest().body("Грешка: Не е изпратен рейтинг.");
        }

        // Търсим продукта в базата данни по неговото ID
        Optional<Product> productOptional = productRepository.findById(id);

        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            // Задаваме новия рейтинг
            product.setRating(newRating);
            // Запазваме промените в базата (MySQL)
            productRepository.save(product);

            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    // 4. Обновяване на ЦЕЛИЯ продукт (име, цена, категория и т.н.)
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        return productRepository.findById(id).map(existingProduct -> {
            // Обновяваме полетата със стойностите, изпратени от Vue
            existingProduct.setName(productDetails.getName());
            existingProduct.setPrice(productDetails.getPrice());
            existingProduct.setDescription(productDetails.getDescription());
            existingProduct.setImageUrl(productDetails.getImageUrl());
            existingProduct.setCategory(productDetails.getCategory());

            // Запазваме обновения продукт в базата данни
            Product savedProduct = productRepository.save(existingProduct);
            return ResponseEntity.ok(savedProduct);
        }).orElse(ResponseEntity.notFound().build());
    }
}