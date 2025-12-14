package com.gdelis.spring.kafka.configuration.kafka;

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
   NewTopic usersTopic(@Value("${kafka.topics.users.partitions}") final Integer partitions,
                       @Value("${kafka.topics.users.name}") final String name) {
      return TopicBuilder.name(name)
                         .partitions(partitions)
                         .replicas(1)
                         .build();
   }
   
   @Bean
   NewTopic enhancedUsersTopics(@Value("${kafka.topics.enhanced-users.partitions}") final Integer partitions,
                                @Value("${kafka.topics.enhanced-users.name}") final String name) {
      return TopicBuilder.name(name)
                         .partitions(partitions)
                         .replicas(1)
                         .build();
   }
   
   @Bean
   NewTopic addressesTopics(@Value("${kafka.topics.addresses.partitions}") final Integer partitions,
                            @Value("${kafka.topics.addresses.name}") final String name) {
      return TopicBuilder.name(name)
                         .partitions(partitions)
                         .replicas(1)
                         .build();
   }
}
