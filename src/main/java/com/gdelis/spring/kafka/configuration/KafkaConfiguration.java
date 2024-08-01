package com.gdelis.spring.kafka.configuration;

import com.gdelis.spring.kafka.CountryEnum;
import com.gdelis.spring.kafka.interceptor.AuthorHeaderProducerInterceptor;
import com.gdelis.spring.kafka.interceptor.DateHeaderProducerInterceptor;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.util.Properties;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {

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
                         .partitions(1)
                         .replicas(1)
                         .build();
   }

   @Bean
   Properties kafkaProducerProperties() {
      Properties kafkaProperties = new Properties();

      kafkaProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
      kafkaProperties.put(ProducerConfig.CLIENT_ID_CONFIG, "producer-user-group-1");
      //kafkaProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
      kafkaProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
      kafkaProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
      kafkaProperties.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, String.join(",",
                                                                                 DateHeaderProducerInterceptor.class.getName(),
                                                                                 AuthorHeaderProducerInterceptor.class.getName()));
      kafkaProperties.put("schema.registry.url", "http://localhost:8081");

      return kafkaProperties;
   }

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
   Schema usersAvroSchema() {

      return SchemaBuilder.record("User")
                          .fields()
                          .requiredString("username")
                          .requiredString("firstName")
                          .requiredString("lastName")
                          .requiredString("email")
                          .name("telephone")
                          .type()
                          .nullable()
                          .stringType()
                          .noDefault()
                          .name("country")
                          .type()
                          .enumeration("CountryEnum")
                          .symbols(CountryEnum.GR.getAbbreviation(), CountryEnum.USA.getAbbreviation(),
                                   CountryEnum.UK.getAbbreviation(), CountryEnum.OTHER.getAbbreviation())
                          .enumDefault(CountryEnum.OTHER.getAbbreviation())
                          .name("details")
                          .type()
                          .nullable()
                          .map()
                          .values()
                          .stringType()
                          .noDefault()
                          .endRecord();
   }

   @Bean
   KafkaProducer<String, GenericRecord> usersKafkaProducer(
       @Qualifier("kafkaProducerProperties") final Properties producerProperties) {
      return new KafkaProducer<>(producerProperties);
   }

   @Bean
   KafkaConsumer<String, GenericRecord> usersKafkaConsumer(
       @Qualifier("usersKafkaConsumerProperties") final Properties consumerProperties) {
      return new KafkaConsumer<>(consumerProperties);
   }
}
