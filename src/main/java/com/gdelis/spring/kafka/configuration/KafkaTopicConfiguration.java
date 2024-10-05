package com.gdelis.spring.kafka.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfiguration {
   
   /**
    * Causes the topic to be created on the broker; it is not needed if the topic already exists.
    *
    * @return
    */
   @Bean
   NewTopic users(@Value("${kafka.users.topic.partitions}") final Integer partitionCount,
                  @Value("${kafka.users.topic.name}") final String usersTopic) {
      return TopicBuilder.name(usersTopic)
                         .partitions(partitionCount)
                         .replicas(1)
                         .build();
   }
}
