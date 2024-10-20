package com.gdelis.spring.kafka.controller;

import com.gdelis.spring.kafka.Product;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductsController {
   
   private final ProductsHttpService productsHttpService;
   
   public ProductsController(final ProductsHttpService productsHttpService) {
      this.productsHttpService = productsHttpService;
   }
   
   @GetMapping
   ResponseEntity<List<Product>> getProducts() {
      return productsHttpService.getProducts();
   }
   
   @PostMapping
   ResponseEntity<Product> createProduct(@RequestBody Product product) {
      return productsHttpService.addProduct(product);
   }
   
   @DeleteMapping("/{id}")
   ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
      return productsHttpService.deleteProduct(id);
   }
}
