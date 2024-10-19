package com.gdelis.spring.kafka.exception;

public class ClientException extends RuntimeException {
   public ClientException(final String message) {
      super(message);
   }
}
