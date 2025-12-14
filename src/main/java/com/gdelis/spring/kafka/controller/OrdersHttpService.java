package com.gdelis.spring.kafka.controller;

import com.gdelis.spring.kafka.domain.Order;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/orders")
public interface OrdersHttpService {
   
   @GetExchange
   ResponseEntity<List<Order>> getOrders();
   
   @GetExchange("/{id}")
   ResponseEntity<Order> getOrderById(@PathVariable Integer id);
   
   @DeleteExchange("/{id}")
   ResponseEntity<Void> deleteOrderById(@PathVariable Integer id);
}
