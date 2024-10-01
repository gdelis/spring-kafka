package com.gdelis.spring.kafka.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelephoneValidator implements ConstraintValidator<Telephone, String> {
   
   @Override
   public boolean isValid(final String telephone, final ConstraintValidatorContext constraintValidatorContext) {
      return telephone != null && telephone.length() == 10;
   }
}
