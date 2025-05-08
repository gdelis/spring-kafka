package com.gdelis.spring.kafka.exception;

public class ServerException extends RuntimeException {
   public ServerException(final String message) {
      super(message);
   }
}
