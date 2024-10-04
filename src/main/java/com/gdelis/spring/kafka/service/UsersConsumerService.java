package com.gdelis.spring.kafka.service;

import com.gdelis.spring.kafka.CountryEnum;
import com.gdelis.spring.kafka.UserDetails;
import com.gdelis.spring.kafka.UserTypeEnum;
import com.gdelis.spring.kafka.repository.UserDetailsRepository;
import java.time.Duration;
import java.util.List;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
public class UsersConsumerService {
   
   @Bean
   @DependsOn("usersProducerRunner")
   public ApplicationRunner usersConsumerRunner(
       @Qualifier("usersKafkaConsumer") KafkaConsumer<String, GenericRecord> consumer,
       final UserDetailsRepository repository,
       @Value("${kafka.users.topic}") String topic) {
      
      return args -> {
         consumer.subscribe(List.of(topic));
         
         try {
            while (true) {
               ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofSeconds(1));
               for (ConsumerRecord<String, GenericRecord> r : records) {
                  System.out.printf("group = user-group-1, " + "offset = %d, " + "key = %s, " + "value = %s \n",
                                    r.offset(),
                                    r.key(),
                                    r.value());
                  
                  UserDetails userDetails = userDetailsConverter(r);
                  repository.save(userDetails);
               }
            }
         } finally {
            consumer.close();
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
