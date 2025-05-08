package com.gdelis.spring.kafka.configuration;

import com.gdelis.spring.kafka.CountryEnum;
import com.gdelis.spring.kafka.UserTypeEnum;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AvroSchemaConfiguration {
   
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
                          .symbols(CountryEnum.GR.getAbbreviation(),
                                   CountryEnum.USA.getAbbreviation(),
                                   CountryEnum.UK.getAbbreviation(),
                                   CountryEnum.OTHER.getAbbreviation())
                          .enumDefault(CountryEnum.OTHER.getAbbreviation())
                          .name("type")
                          .type()
                          .enumeration("UserTypeEnum")
                          .symbols(UserTypeEnum.USER.name(), UserTypeEnum.ADMIN.name())
                          .enumDefault(UserTypeEnum.USER.name())
                          .name("details")
                          .type()
                          .nullable()
                          .map()
                          .values()
                          .stringType()
                          .noDefault()
                          .endRecord();
   }
}
