package com.gdelis.spring.kafka.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService {

   private final KafkaTemplate<String, String> kafkaTemplate;

   public UserService(final KafkaTemplate<String, String> kafkaTemplate) {
      this.kafkaTemplate = kafkaTemplate;
   }

   public void sendUser(final String message) {
      kafkaTemplate.send("users", message);
   }
}
