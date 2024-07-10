package com.gdelis.spring.kafka.configuration;

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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {

   @Value("${kafka.users.topic}")
   private String topic;

   /**
    * Causes the topic to be created on the broker; it is not needed if the topic already exists.
    *
    * @return
    */
   @Bean
   public NewTopic users() {
      return TopicBuilder.name(topic)
                         .partitions(1)
                         .replicas(1)
                         .build();
   }

   @Bean
   public Properties kafkaProducerProperties() {
      Properties kafkaProperties = new Properties();

      kafkaProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
      kafkaProperties.put(ProducerConfig.CLIENT_ID_CONFIG, "spring-kafka");
      kafkaProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
      kafkaProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
      kafkaProperties.put("schema.registry.url", "http://localhost:8081");

      return kafkaProperties;
   }

   @Bean
   public Properties kafkaConsumerProperties() {
      Properties kafkaProperties = new Properties();

      kafkaProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
      kafkaProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");
      kafkaProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
      kafkaProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
      kafkaProperties.put("schema.registry.url", "http://localhost:8081");
      kafkaProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

      return kafkaProperties;
   }

   @Bean
   public Schema userAvroSchema() {
      return SchemaBuilder.record("User")
                          .fields()
                          .requiredString("firstName")
                          .requiredString("lastName")
                          .name("telephone")
                          .type()
                          .nullable()
                          .stringType()
                          .noDefault()
                          .endRecord();
   }

   @Bean
   public KafkaProducer<String, GenericRecord> userKafkaProducer(
       @Qualifier("kafkaProducerProperties") final Properties producerProperties) {

      return new KafkaProducer<>(producerProperties);
   }

   @Bean
   public KafkaConsumer<String, GenericRecord> userKafkaConsumer(
       @Qualifier("kafkaConsumerProperties") final Properties consumerProperties) {
      return new KafkaConsumer<>(consumerProperties);
   }
}
