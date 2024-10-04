package com.gdelis.spring.kafka.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfiguration {
   
   @Value("${kafka.users.topic}")
   private String usersTopic;
   
   /**
    * Causes the topic to be created on the broker; it is not needed if the topic already exists.
    *
    * @return
    */
   @Bean
   NewTopic users() {
      return TopicBuilder.name(usersTopic)
                         .partitions(3)
                         .replicas(1)
                         .build();
   }
}
