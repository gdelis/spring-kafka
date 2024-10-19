package com.gdelis.spring.kafka.controller;

import com.gdelis.spring.kafka.Product;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/products")
public interface ProductsHttpService {
   
   @GetExchange
   ResponseEntity<List<Product>> getProducts();
}
