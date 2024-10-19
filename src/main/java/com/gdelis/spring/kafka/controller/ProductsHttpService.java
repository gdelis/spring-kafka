package com.gdelis.spring.kafka.controller;

import com.gdelis.spring.kafka.Product;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/products")
public interface ProductsHttpService {
   
   @GetExchange
   ResponseEntity<List<Product>> getProducts();
   
   @PostExchange
   ResponseEntity<Product> addProduct(@RequestBody Product product);
   
   @DeleteExchange("/{id}")
   ResponseEntity<Void> deleteProduct(@PathVariable("id") Integer id);
}
