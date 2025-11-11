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
   NewTopic usersSink(@Value("${kafka.users.topic.partitions}") final Integer partitions,
                      @Value("${kafka.users.topic.sink.name}") final String name) {
      return TopicBuilder.name(name)
                         .partitions(partitions)
                         .replicas(1)
                         .build();
   }
   
   @Bean
   NewTopic usersSource(@Value("${kafka.users.topic.partitions}") final Integer partitions,
                        @Value("${kafka.users.topic.source.name}") final String name) {
      return TopicBuilder.name(name)
                         .partitions(partitions)
                         .replicas(1)
                         .build();
   }
   
   @Bean
   NewTopic addressesSource(@Value("${kafka.addresses.topic.partitions}") final Integer partitions,
                            @Value("${kafka.addresses.topic.name}") final String name) {
      return TopicBuilder.name(name)
                         .partitions(partitions)
                         .replicas(1)
                         .build();
   }
}
