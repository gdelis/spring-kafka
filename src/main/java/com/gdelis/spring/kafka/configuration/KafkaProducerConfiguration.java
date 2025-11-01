package com.gdelis.spring.kafka.configuration;

import com.gdelis.spring.kafka.interceptor.AuthorHeaderProducerInterceptor;
import com.gdelis.spring.kafka.interceptor.DateHeaderProducerInterceptor;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
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
      
      // RETRY MECHANISM CONFIGURATION:
      // configure the number of retries in case the producer receives an error:
      // kafkaProperties.put(ProducerConfig.RETRIES_CONFIG, 3);
      // Change the default waiting time between a failed request and then next retry (default value is 100ms):
      // kafkaProperties.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, "200");
      // Upper bound on how long the producer will retry sending before giving up and throwing an error:
      // kafkaProperties.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 30000);
      // Determines when a record is considered acknowledged — affects what’s considered a retryable failure. (acks=all is most reliable).
      // kafkaProperties.put(ProducerConfig.ACKS_CONFIG, "all");
      
      // kafkaProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
      kafkaProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
      kafkaProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
      kafkaProperties.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,
                          String.join(",", DateHeaderProducerInterceptor.class.getName(), AuthorHeaderProducerInterceptor.class.getName()));
      kafkaProperties.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
      
      return kafkaProperties;
   }
   
   @Bean
   KafkaProducer<String, GenericRecord> usersKafkaProducer(@Qualifier("userKafkaProducerProperties") final Properties producerProperties) {
      return new KafkaProducer<>(producerProperties);
   }
}
