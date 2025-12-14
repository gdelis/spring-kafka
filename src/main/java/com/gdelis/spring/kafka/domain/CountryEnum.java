package com.gdelis.spring.kafka.domain;

import lombok.Getter;

@Getter
public enum CountryEnum {
   
   UK("United Kingdom", "UK", "Europe"),
   GR("Greece", "GR", "Europe"),
   USA("United States of America", "US", "North America"),
   OTHER("Other", "Other", null);
   
   private String name;
   private String abbreviation;
   private String continent;
   
   CountryEnum(final String name, final String abbreviation, final String continent) {
      this.name = name;
      this.abbreviation = abbreviation;
      this.continent = continent;
   }
   
   public static CountryEnum getCountryEnumFromAbbreviationValue(final String abbreviation) {
      for (CountryEnum countryEnum : CountryEnum.values()) {
         if (countryEnum.getAbbreviation()
                        .equals(abbreviation)) {
            return countryEnum;
         }
      }
      return CountryEnum.OTHER;
   }
}
