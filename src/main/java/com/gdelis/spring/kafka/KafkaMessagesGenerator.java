package com.gdelis.spring.kafka;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessagesGenerator {
   
   private final Callback genericCallback = (recordMetadata, e) -> {
      System.out.println("recordMetadata = " + recordMetadata.toString());
      
      if (e != null) {
         System.out.println(e.getMessage());
      }
   };
   
   @Bean
   public ApplicationRunner usersProducerRunner(@Qualifier("usersKafkaProducer") KafkaProducer<String, GenericRecord> producer,
                                                @Qualifier("usersAvroSchema") Schema userAvroSchema,
                                                @Value("${kafka.topics.users.name}") String topic) {
      
      return args -> {
         List<GenericRecord> genericRecords = generateUsersKafkaMessages(userAvroSchema);
         
         genericRecords.stream()
                       .map(s -> new ProducerRecord<>(topic, getUsersKafkaMessageKey(s), s))
                       .forEach(record -> {
                          record.headers()
                                .add("producer-header-author", "George Delis".getBytes(StandardCharsets.UTF_8));
                          // This is an asynchronous send (because we are using a callback method):
                          producer.send(record, genericCallback);
                       });
      };
   }
   
   @Bean
   public ApplicationRunner addressProducerRunner(@Qualifier("addressesKafkaProducer") KafkaProducer<String, GenericRecord> producer,
                                                  @Qualifier("addressesAvroSchema") Schema addressesAvroSchema,
                                                  @Value("${kafka.topics.addresses.name}") String topic) {
      
      return args -> {
         List<GenericRecord> genericRecords = generateAddressesKafkaMessages(addressesAvroSchema);
         
         genericRecords.stream()
                       .map(s -> new ProducerRecord<>(topic, getAddressesKafkaMessageKey(s), s))
                       .forEach(record -> {
                          record.headers()
                                .add("producer-header-author", "George Delis".getBytes(StandardCharsets.UTF_8));
                          // This is an asynchronous send (because we are using a callback method):
                          producer.send(record, genericCallback);
                       });
      };
   }
   
   private String getUsersKafkaMessageKey(final GenericRecord s) {
      if (s.get("username") != null) {
         return (String) s.get("username");
      }
      
      return "random";
   }
   
   private String getAddressesKafkaMessageKey(final GenericRecord s) {
      if (s.get("postcode") != null) {
         return (String) s.get("postcode");
      }
      
      return "unknown";
   }
   
   private List<GenericRecord> generateUsersKafkaMessages(final Schema userAvroSchema) {
      List<GenericRecord> records = new ArrayList<>();
      
      GenericRecordBuilder genericRecordBuilder = new GenericRecordBuilder(userAvroSchema);
      
      for (int i = 0; i < 20; i++) {
         genericRecordBuilder.set("username", "george/delis-" + i);
         genericRecordBuilder.set("firstName", "George-" + i);
         genericRecordBuilder.set("lastName", "Delis-" + i);
         genericRecordBuilder.set("email", "gdelis1989@gmail.com");
         genericRecordBuilder.set("telephone", "222-222-2222-" + i);
         
         GenericData.EnumSymbol type = new GenericData.EnumSymbol(userAvroSchema.getField("type")
                                                                                .schema(), UserTypeEnum.ADMIN.name());
         genericRecordBuilder.set("type", type);
         genericRecordBuilder.set("postcode", "postcode-" + i);
         genericRecordBuilder.set("details", Map.of("city", "tripoli", "providence", "arcadia"));
         
         records.add(genericRecordBuilder.build());
      }
      
      return records;
   }
   
   private List<GenericRecord> generateAddressesKafkaMessages(final Schema addressesAvroSchema) {
      List<GenericRecord> records = new ArrayList<>();
      
      GenericRecordBuilder genericRecordBuilder = new GenericRecordBuilder(addressesAvroSchema);
      
      for (int i = 0; i < 20; i++) {
         genericRecordBuilder.set("postcode", "postcode-" + i);
         genericRecordBuilder.set("address", "address-" + i);
         genericRecordBuilder.set("number", i);
         genericRecordBuilder.set("flat", "flat-" + i);
         genericRecordBuilder.set("building", "building-" + i);
         
         GenericData.EnumSymbol country = new GenericData.EnumSymbol(addressesAvroSchema.getField("country")
                                                                                        .schema(), CountryEnum.GR.getAbbreviation());
         genericRecordBuilder.set("country", country);
         
         records.add(genericRecordBuilder.build());
      }
      
      return records;
   }
}
