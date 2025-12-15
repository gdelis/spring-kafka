package com.gdelis.spring.kafka.configuration.kafka;

import com.gdelis.spring.kafka.domain.CountryEnum;
import com.gdelis.spring.kafka.domain.UserTypeEnum;
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
                          .name("type")
                          .type()
                          .enumeration("UserTypeEnum")
                          .symbols(UserTypeEnum.USER.name(), UserTypeEnum.ADMIN.name())
                          .enumDefault(UserTypeEnum.USER.name())
                          .name("country")
                          .type()
                          .enumeration("CountryEnum")
                          .symbols(CountryEnum.GR.name(),
                                   CountryEnum.USA.name(),
                                   CountryEnum.UK.name(),
                                   CountryEnum.OTHER.name())
                          .enumDefault(CountryEnum.OTHER.name())
                          .requiredString("postcode")
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
   Schema addressesAvroSchema() {
      return SchemaBuilder.record("Address")
                          .fields()
                          .requiredString("postcode")
                          .requiredString("address")
                          .requiredInt("number")
                          .optionalString("flat")
                          .optionalString("building")
                          .name("country")
                          .type()
                          .enumeration("CountryEnum")
                          .symbols(CountryEnum.GR.getAbbreviation(),
                                   CountryEnum.USA.getAbbreviation(),
                                   CountryEnum.UK.getAbbreviation(),
                                   CountryEnum.OTHER.getAbbreviation())
                          .enumDefault(CountryEnum.OTHER.getAbbreviation())
                          .endRecord();
   }
   
   @Bean
   Schema enhancedUsersAvroSchema() {
      return SchemaBuilder.record("EnhancedUser")
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
                          .name("type")
                          .type()
                          .enumeration("UserTypeEnum")
                          .symbols(UserTypeEnum.USER.name(), UserTypeEnum.ADMIN.name())
                          .enumDefault(UserTypeEnum.USER.name())
                          .requiredString("postcode")
                          .optionalString("address")
                          .optionalInt("number")
                          .optionalString("flat")
                          .optionalString("building")
                          .name("country")
                          .type()
                          .enumeration("CountryEnum")
                          .symbols(CountryEnum.GR.getAbbreviation(),
                                   CountryEnum.USA.getAbbreviation(),
                                   CountryEnum.UK.getAbbreviation(),
                                   CountryEnum.OTHER.getAbbreviation())
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
}
