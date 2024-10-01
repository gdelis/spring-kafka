package com.gdelis.spring.kafka.exception;

public class KafkaException extends RuntimeException {
   
   public KafkaException(final String message) {
      super(message);
   }
}
