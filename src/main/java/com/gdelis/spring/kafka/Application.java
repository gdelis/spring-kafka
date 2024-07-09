package com.gdelis.spring.kafka;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class Application {

   public static void main(String[] args) {
      SpringApplication.run(Application.class, args);
   }

   @KafkaListener(id = "salute-consumer", topics = "users")
   public void salute(String user) {
      System.out.println("Hello user: " + user);
   }

   @KafkaListener(id = "name-consumer", topics = "users")
   public void name(String user) {
      System.out.println("Welcome home user: " + user);
   }
}
