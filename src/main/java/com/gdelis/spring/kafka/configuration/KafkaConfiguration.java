package com.gdelis.spring.kafka.configuration;

import java.util.Properties;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {

   /**
    * Causes the topic to be created on the broker; it is not needed if the topic already exists.
    *
    * @return
    */
   @Bean
   public NewTopic users() {
      return TopicBuilder.name("users")
                         .partitions(1)
                         .replicas(1)
                         .build();
   }

   @Bean
   public KafkaProducer<String, String> stringKafkaProducer() {
      Properties kafkaProperties = new Properties();

      kafkaProperties.put("bootstrap.servers", "localhost:9092");
      kafkaProperties.put("client.id", "spring-kafka");
      kafkaProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
      kafkaProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

      return new KafkaProducer<>(kafkaProperties);
   }
}
