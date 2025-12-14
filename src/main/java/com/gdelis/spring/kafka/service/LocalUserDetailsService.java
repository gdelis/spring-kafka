package com.gdelis.spring.kafka.service;

import com.gdelis.spring.kafka.domain.UserDetails;
import com.gdelis.spring.kafka.exception.KafkaException;
import com.gdelis.spring.kafka.repository.UserDetailsRepository;
import java.util.List;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class LocalUserDetailsService implements UserDetailsService {
   
   private final KafkaProducer<String, GenericRecord> kafkaProducer;
   private final Schema userAvroSchema;
   private final UserDetailsRepository userDetailsRepository;
   
   public LocalUserDetailsService(
       @Qualifier("usersKafkaProducer") final KafkaProducer<String, GenericRecord> kafkaProducer,
       @Qualifier("usersAvroSchema") final Schema userAvroSchema,
       final UserDetailsRepository userDetailsRepository) {
      this.kafkaProducer = kafkaProducer;
      this.userAvroSchema = userAvroSchema;
      this.userDetailsRepository = userDetailsRepository;
   }
   
   @Override
   public UserDetails createUserDetails(final UserDetails user) {
      GenericRecordBuilder genericRecordBuilder = new GenericRecordBuilder(userAvroSchema);
      
      genericRecordBuilder.set("username", user.username());
      genericRecordBuilder.set("firstName", user.firstName());
      genericRecordBuilder.set("lastName", user.lastName());
      genericRecordBuilder.set("email", user.email());
      genericRecordBuilder.set("telephone", user.telephone());
      genericRecordBuilder.set("postcode", user.postcode());
      genericRecordBuilder.set("details", user.details());
      
      if (user.type() != null) {
         GenericData.EnumSymbol type = new GenericData.EnumSymbol(userAvroSchema.getField("type")
                                                                                .schema(), user.type());
         genericRecordBuilder.set("type", type);
      }
      
      ProducerRecord<String, GenericRecord> userProducerRecord =
          new ProducerRecord<>("users", user.username(), genericRecordBuilder.build());
      
      kafkaProducer.send(userProducerRecord, (metadata, exception) -> {
         if (exception != null) {
            System.out.println("exception = " + exception.getMessage());
            
            throw new KafkaException("kafka exception");
         }
      });
      
      return user;
   }
   
   @Override
   public List<UserDetails> getAllUserDetails() {
      return userDetailsRepository.findAll();
   }
}
