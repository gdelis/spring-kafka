package com.gdelis.spring.kafka;

import java.util.ArrayList;
import java.util.List;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

@SpringBootApplication
public class Application {

   @Value("${kafka.users.topic}")
   private String topic;

   public static void main(String[] args) {
      SpringApplication.run(Application.class, args);
   }

   @Bean
   public ApplicationRunner producerRunner(
       @Qualifier("userKafkaProducer") KafkaProducer<String, GenericRecord> producer,
       @Qualifier("userAvroSchema") Schema userAvroSchema) {

      return args -> {
         List<GenericRecord> genericRecords = generateRecords(userAvroSchema);

         genericRecords.stream()
                       .map(s -> new ProducerRecord<String, GenericRecord>("users", null, s))
                       .forEach(record -> producer.send(record, (recordMetadata, e) -> {
                          System.out.println("recordMetadata = " + recordMetadata.toString());

                          if (e != null) {
                             System.out.println(e.getMessage());
                          }
                       }));
      };
   }

   @Bean
   @DependsOn("producerRunner")
   public ApplicationRunner consumerRunner(
       @Qualifier("userKafkaConsumer") KafkaConsumer<String, GenericRecord> consumer) {
      return args -> {
         consumer.subscribe(List.of(topic));

         try {
            while (true) {
               ConsumerRecords<String, GenericRecord> records = consumer.poll(100);
               for (ConsumerRecord<String, GenericRecord> r : records) {
                  System.out.printf("offset = %d, key = %s, value = %s \n", r.offset(), r.key(),
                                    r.value());
               }
            }
         } finally {
            consumer.close();
         }
      };
   }

   private List<GenericRecord> generateRecords(final Schema userAvroSchema) {
      List<GenericRecord> records = new ArrayList<>();

      GenericRecordBuilder genericRecordBuilder = new GenericRecordBuilder(userAvroSchema);

      for (int i = 0; i < 10; i++) {
         genericRecordBuilder.set("firstName", "George-" + i);
         genericRecordBuilder.set("lastName", "Delis-" + i);
         genericRecordBuilder.set("telephone", "222-222-2222-" + i);

         records.add(genericRecordBuilder.build());
      }

      return records;
   }
}

