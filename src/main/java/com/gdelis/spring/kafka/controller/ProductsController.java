package com.gdelis.spring.kafka.controller;

import com.gdelis.spring.kafka.Product;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductsController {
   
   private final ProductsHttpService productsHttpService;
   
   public ProductsController(final ProductsHttpService productsHttpService) {
      this.productsHttpService = productsHttpService;
   }
   
   @GetMapping
   ResponseEntity<List<Product>> getProducts() {
      return productsHttpService.getProducts();
   }
}
