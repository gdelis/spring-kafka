package com.gdelis.spring.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
public class Application {

   public static void main(String[] args) {
      SpringApplication.run(Application.class, args);
   }

   @KafkaListener(id = "salute-consumer", topics = "users")
   public void salute(String user) {
      System.out.println("Hello user: " + user);
   }

   @Bean
   public ApplicationRunner runner(@Qualifier("stringKafkaProducer") KafkaProducer<String, String> producer) {

      ProducerRecord<String, String> record = new ProducerRecord<>("users", "name", "George");

      return args -> producer.send(record, (recordMetadata, e) -> {
         System.out.println("recordMetadata = " + recordMetadata);

         if (e != null) {
            System.out.println(e.getMessage());
         }
      });
   }
}
