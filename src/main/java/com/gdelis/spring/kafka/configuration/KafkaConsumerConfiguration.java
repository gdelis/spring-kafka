package com.gdelis.spring.kafka.configuration;

import com.gdelis.spring.kafka.CountryEnum;
import com.gdelis.spring.kafka.UserDetails;
import com.gdelis.spring.kafka.UserTypeEnum;
import com.gdelis.spring.kafka.interceptor.HeadersConsumerInterceptor;
import com.gdelis.spring.kafka.repository.UserDetailsRepository;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class KafkaConsumerConfiguration {
   
   // We just log any error during the offset commit - check book
   private final OffsetCommitCallback offsetCommitCallback = (map, e) -> {
      if (e != null) {
         System.out.println("Exception: " + e.getMessage());
      }
      
      //map.forEach((topicPartition, offsetAndMetadata) -> {
      //   System.out.println("topic name: " + topicPartition.topic() + " and partition: " + topicPartition.partition());
      //   System.out.println("Offset: " + offsetAndMetadata.offset());
      //   System.out.println("Metadata: " + offsetAndMetadata.metadata());
      //   System.out.println("Leader epoch: " + offsetAndMetadata.leaderEpoch());
      //});
   };
   
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
      
      // Automatic commit configuration:
      // kafkaProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
      // kafkaProperties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
      
      // Manual and asynchronous commit configuration:
      kafkaProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
      
      return kafkaProperties;
   }
   
   @Bean
   @DependsOn("usersProducerRunner")
   ApplicationRunner usersKafkaConsumer(@Qualifier("usersKafkaConsumerProperties") final Properties consumerProperties,
                                        @Value("${kafka.users.topic.source.name}") final String topic,
                                        @Value("${kafka.users.topic.partitions}") final Integer partitions,
                                        @Value("${kafka.users.topic.polling}") final Integer polling,
                                        final UserDetailsRepository userDetailsRepository) {
      
      // This should be an external attribute to trigger when the process of kafka messages should stop
      // For example, when kafka is re-balancing
      Boolean closing = false;
      
      return args -> {
         for (int i = 0; i < partitions; i++) {
            new Thread(() -> {
               
               try (KafkaConsumer<String, GenericRecord> consumer = new KafkaConsumer<>(consumerProperties);) {
                  consumer.subscribe(List.of(topic));
                  
                  // Add custom rebalance listener:
                  //consumer.subscribe(List.of(topic), consumerRebalanceListener(consumer));
                  
                  // Offset commit policy -> Combining synchronous and asynchronous commits
                  while (!closing) {
                     ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofMillis(polling));
                     
                     for (ConsumerRecord<String, GenericRecord> record : records) {
                        System.out.printf("Thread %s consumed record with key %s and value %s%n",
                                          Thread.currentThread()
                                                .threadId(),
                                          record.key(),
                                          record.value());
                        
                        UserDetails userDetails = userDetailsConverter(record);
                        userDetailsRepository.save(userDetails);
                     }
                     
                     consumer.commitAsync(offsetCommitCallback);
                  }
                  
                  // We use synchronous commits when we terminate the process (or before re-balance)
                  consumer.commitSync();
               } catch (Exception e) {
                  e.printStackTrace();
               }
            }).start();
         }
      };
   }
   
   private ConsumerRebalanceListener consumerRebalanceListener(final KafkaConsumer<String, GenericRecord> consumer) {
      return new ConsumerRebalanceListener() {
         @Override
         public void onPartitionsRevoked(final Collection<TopicPartition> collection) {
            System.out.println("onPartitionsRevoked");
            consumer.commitSync();
         }
         
         @Override
         public void onPartitionsAssigned(final Collection<TopicPartition> collection) {
            System.out.println("onPartitionsAssigned");
         }
      };
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
