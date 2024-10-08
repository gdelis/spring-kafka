package com.gdelis.spring.kafka;

import com.gdelis.spring.kafka.repository.UserDetailsRepository;
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
   @DependsOn("usersProducerRunner")
   public ApplicationRunner usersConsumerRunner(
       @Qualifier("usersKafkaConsumer") KafkaConsumer<String, GenericRecord> consumer,
       final UserDetailsRepository repository) {
      return args -> {
         consumer.subscribe(List.of(topic));

         try {
            while (true) {
               ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofSeconds(1));
               for (ConsumerRecord<String, GenericRecord> r : records) {
                  System.out.printf("group = user-group-1, "
                                        + "offset = %d, "
                                        + "key = %s, "
                                        + "value = %s \n", r.offset(), r.key(), r.value());

                  UserDetails userDetails = userDetailsConverter(r);
                  repository.save(userDetails);
               }
            }
         } finally {
            consumer.close();
         }
      };
   }

   @Bean
   public ApplicationRunner usersProducerRunner(
       @Qualifier("usersKafkaProducer") KafkaProducer<String, GenericRecord> producer,
       @Qualifier("usersAvroSchema") Schema userAvroSchema) {

      return args -> {
         List<GenericRecord> genericRecords = generateRecords(userAvroSchema);

         genericRecords.stream()
                       .map(s -> new ProducerRecord<String, GenericRecord>("users", s))
                       .forEach(record -> producer.send(record, (recordMetadata, e) -> {
                          System.out.println("recordMetadata = " + recordMetadata.toString());

                          if (e != null) {
                             System.out.println(e.getMessage());
                          }
                       }));
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

         GenericData.EnumSymbol country = new GenericData.EnumSymbol(
             userAvroSchema.getField("country")
                           .schema(),
             CountryEnum.GR.getAbbreviation());
         genericRecordBuilder.set("country", country);

         GenericData.EnumSymbol type = new GenericData.EnumSymbol(
             userAvroSchema.getField("type")
                           .schema(),
             UserTypeEnum.ADMIN.name());
         genericRecordBuilder.set("type", type);
         genericRecordBuilder.set("details", Map.of("city", "tripoli", "providence", "arcadia"));

         records.add(genericRecordBuilder.build());
      }

      return records;
   }

   private UserDetails userDetailsConverter(final ConsumerRecord<String, GenericRecord> r) {
      CountryEnum country = CountryEnum.getCountryEnumFromAbbreviationValue(r.value()
                                                                             .get("country")
                                                                             .toString());

      UserTypeEnum type = UserTypeEnum.valueOf(r.value()
                                                .get("type")
                                                .toString());

      return new UserDetails(
          r.value()
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