package com.gdelis.spring.kafka.controller;

import com.gdelis.spring.kafka.UserDetails;
import com.gdelis.spring.kafka.exception.KafkaException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserDetailsController {

   private final KafkaProducer<String, GenericRecord> kafkaProducer;
   private final Schema userAvroSchema;

   public UserDetailsController(
       @Qualifier("usersKafkaProducer") final KafkaProducer<String, GenericRecord> kafkaProducer,
       @Qualifier("usersAvroSchema") final Schema userAvroSchema) {
      this.kafkaProducer = kafkaProducer;
      this.userAvroSchema = userAvroSchema;
   }

   @PostMapping
   public ResponseEntity<UserDetails> createUser(@RequestBody final UserDetails user) {

      GenericRecordBuilder genericRecordBuilder = new GenericRecordBuilder(userAvroSchema);

      genericRecordBuilder.set("username", user.username());
      genericRecordBuilder.set("firstName", user.firstName());
      genericRecordBuilder.set("lastName", user.lastName());
      genericRecordBuilder.set("email", user.email());
      genericRecordBuilder.set("telephone", user.telephone());
      if (user.country() != null) {
         GenericData.EnumSymbol country = new GenericData.EnumSymbol(
             userAvroSchema.getField("country")
                           .schema(),
             user.country()
                 .getAbbreviation());
         genericRecordBuilder.set("country", country);
      }
      genericRecordBuilder.set("details", user.details());

      ProducerRecord<String, GenericRecord> userProducerRecord =
          new ProducerRecord<>("users", genericRecordBuilder.build());

      kafkaProducer.send(userProducerRecord, (metadata, exception) -> {
         if (exception != null) {
            System.out.println("exception = " + exception.getMessage());

            throw new KafkaException("kafka exception");
         }
      });

      return ResponseEntity.ok(user);
   }
}
