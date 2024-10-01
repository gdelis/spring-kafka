package com.gdelis.spring.kafka.utils;

import com.gdelis.spring.kafka.CountryEnum;
import java.util.Arrays;
import org.springframework.core.convert.converter.Converter;

public class StringToCountryEnumConverter implements Converter<String, CountryEnum> {
   
   @Override
   public CountryEnum convert(final String source) {
      return Arrays.stream(CountryEnum.values())
                   .filter(s -> s.getAbbreviation()
                                 .equalsIgnoreCase(source))
                   .findAny()
                   .orElse(CountryEnum.OTHER);
   }
}
