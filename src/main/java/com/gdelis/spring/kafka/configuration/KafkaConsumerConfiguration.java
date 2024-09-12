package com.gdelis.spring.kafka.configuration;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import java.util.Properties;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConsumerConfiguration {

   @Bean
   Properties usersKafkaConsumerProperties(@Value("${kafka.users.consumer.group.id}") String consumerGroupId) {
      Properties kafkaProperties = new Properties();

      kafkaProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
      kafkaProperties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
      kafkaProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
      //kafkaProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
      kafkaProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
      //kafkaProperties.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, HeadersConsumerInterceptor.class.getName());
      kafkaProperties.put("schema.registry.url", "http://localhost:8081");
      kafkaProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

      return kafkaProperties;
   }

   @Bean
   KafkaConsumer<String, GenericRecord> usersKafkaConsumer(
       @Qualifier("usersKafkaConsumerProperties") final Properties consumerProperties) {
      return new KafkaConsumer<>(consumerProperties);
   }
}
