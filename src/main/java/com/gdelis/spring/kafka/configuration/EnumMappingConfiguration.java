package com.gdelis.spring.kafka.configuration;

import com.gdelis.spring.kafka.utils.StringToCountryEnumConverter;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class EnumMappingConfiguration implements WebMvcConfigurer {

   @Override
   public void addFormatters(final FormatterRegistry registry) {
      ApplicationConversionService.configure(registry);
      registry.addConverter(new StringToCountryEnumConverter());
   }
}
