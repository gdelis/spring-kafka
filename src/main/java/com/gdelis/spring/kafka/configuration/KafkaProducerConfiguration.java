package com.gdelis.spring.kafka.configuration;

import com.gdelis.spring.kafka.interceptor.AuthorHeaderProducerInterceptor;
import com.gdelis.spring.kafka.interceptor.DateHeaderProducerInterceptor;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.util.Properties;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaProducerConfiguration {
   
   @Bean
   Properties userKafkaProducerProperties() {
      Properties kafkaProperties = new Properties();
      
      kafkaProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
      kafkaProperties.put(ProducerConfig.CLIENT_ID_CONFIG, "producer-user-group-1");
      //kafkaProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
      kafkaProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
      kafkaProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
      kafkaProperties.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,
                          String.join(",",
                                      DateHeaderProducerInterceptor.class.getName(),
                                      AuthorHeaderProducerInterceptor.class.getName()));
      kafkaProperties.put("schema.registry.url", "http://localhost:8081");
      
      return kafkaProperties;
   }
   
   @Bean
   KafkaProducer<String, GenericRecord> usersKafkaProducer(
       @Qualifier("userKafkaProducerProperties") final Properties producerProperties) {
      return new KafkaProducer<>(producerProperties);
   }
}
