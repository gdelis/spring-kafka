package com.gdelis.spring.kafka.configuration;

import com.gdelis.spring.kafka.CountryEnum;
import com.gdelis.spring.kafka.UserDetails;
import com.gdelis.spring.kafka.UserTypeEnum;
import com.gdelis.spring.kafka.repository.UserDetailsRepository;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessagesGenerator {
   
   @Bean
   public ApplicationRunner usersProducerRunner(
       @Qualifier("usersKafkaProducer") KafkaProducer<String, GenericRecord> producer,
       @Qualifier("usersAvroSchema") Schema userAvroSchema,
       @Value("${kafka.users.topic}") String topic) {
      
      Callback genericCallback = (recordMetadata, e) -> {
         System.out.println("recordMetadata = " + recordMetadata.toString());
         
         if (e != null) {
            System.out.println(e.getMessage());
         }
      };
      
      return args -> {
         List<GenericRecord> genericRecords = generateRecords(userAvroSchema);
         
         genericRecords.stream()
                       .map(s -> new ProducerRecord<String, GenericRecord>(topic, s))
                       .forEach(record -> {
                          record.headers()
                                .add("producer-header-author", "George Delis".getBytes(StandardCharsets.UTF_8));
                          producer.send(record, genericCallback);
                       });
      };
   }
   
   private List<GenericRecord> generateRecords(final Schema userAvroSchema) {
      List<GenericRecord> records = new ArrayList<>();
      
      GenericRecordBuilder genericRecordBuilder = new GenericRecordBuilder(userAvroSchema);
      
      for (int i = 10; i < 20; i++) {
         genericRecordBuilder.set("username", "george/delis-" + i);
         genericRecordBuilder.set("firstName", "George-" + i);
         genericRecordBuilder.set("lastName", "Delis-" + i);
         genericRecordBuilder.set("email", "gdelis1989@gmail.com");
         genericRecordBuilder.set("telephone", "222-222-2222-" + i);
         
         GenericData.EnumSymbol country = new GenericData.EnumSymbol(userAvroSchema.getField("country")
                                                                                   .schema(),
                                                                     CountryEnum.GR.getAbbreviation());
         genericRecordBuilder.set("country", country);
         
         GenericData.EnumSymbol type = new GenericData.EnumSymbol(userAvroSchema.getField("type")
                                                                                .schema(), UserTypeEnum.ADMIN.name());
         genericRecordBuilder.set("type", type);
         genericRecordBuilder.set("details", Map.of("city", "tripoli", "providence", "arcadia"));
         
         records.add(genericRecordBuilder.build());
      }
      
      return records;
   }
}
