package com.gdelis.spring_kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class Application {

   public static void main(String[] args) {
      SpringApplication.run(Application.class, args);
   }

   @Bean
   public NewTopic topic() {
      return TopicBuilder.name("users")
                         .partitions(1)
                         .replicas(1)
                         .build();
   }

   @KafkaListener(id = "salute-consumer", topics = "users")
   public void salute(String user) {
      System.out.println("Hello user: " + user);
   }

   @KafkaListener(id = "name-consumer", topics = "users")
   public void name(String user) {
      System.out.println("Welcome home user: " + user);
   }

   @Bean
   public ApplicationRunner runner(KafkaTemplate<String, String> template) {
      return args -> {
         template.send("users", "George Delis");
         template.send("users", "John Doe");
         template.send("users", "Jane Doe");
      };
   }
}
