package com.gdelis.spring.kafka.configuration;

import com.gdelis.spring.kafka.CountryEnum;
import com.gdelis.spring.kafka.UserDetails;
import com.gdelis.spring.kafka.UserTypeEnum;
import com.gdelis.spring.kafka.interceptor.HeadersConsumerInterceptor;
import com.gdelis.spring.kafka.repository.UserDetailsRepository;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

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
      kafkaProperties.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, HeadersConsumerInterceptor.class.getName());
      kafkaProperties.put("schema.registry.url", "http://localhost:8081");
      kafkaProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
      
      return kafkaProperties;
   }
   
   @Bean
   @DependsOn("usersProducerRunner")
   KafkaConsumer<String, GenericRecord> usersKafkaConsumer(
       @Qualifier("usersKafkaConsumerProperties") final Properties consumerProperties,
       @Value("${kafka.users.topic}") final String topic,
       final UserDetailsRepository userDetailsRepository) {
      
      for (int i = 0; i < 3; i++) {
         new Thread(() -> {
            KafkaConsumer<String, GenericRecord> consumer = new KafkaConsumer<>(consumerProperties);
            consumer.subscribe(List.of(topic));
            
            while (true) {
               ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofMillis(100));
               for (ConsumerRecord<String, GenericRecord> record : records) {
                  System.out.printf("Thread %s consumed record with key %s and value %s%n",
                                    Thread.currentThread()
                                          .getId(),
                                    record.key(),
                                    record.value());
                  
                  UserDetails userDetails = userDetailsConverter(record);
                  userDetailsRepository.save(userDetails);
               }
            }
         }).start();
      }
      
      return new KafkaConsumer<>(consumerProperties);
   }
   
   private UserDetails userDetailsConverter(final ConsumerRecord<String, GenericRecord> r) {
      CountryEnum country = CountryEnum.getCountryEnumFromAbbreviationValue(r.value()
                                                                             .get("country")
                                                                             .toString());
      UserTypeEnum type = UserTypeEnum.valueOf(r.value()
                                                .get("type")
                                                .toString());
      
      return new UserDetails(r.value()
                              .get("username")
                              .toString(),
                             r.value()
                              .get("firstName")
                              .toString(),
                             r.value()
                              .get("lastName")
                              .toString(),
                             r.value()
                              .get("email")
                              .toString(),
                             r.value()
                              .get("telephone")
                              .toString(),
                             country,
                             type,
                             null);
   }
}
