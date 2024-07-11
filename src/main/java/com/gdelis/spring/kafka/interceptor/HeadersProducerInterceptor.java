package com.gdelis.spring.kafka.interceptor;

import com.gdelis.spring.kafka.User;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HeadersProducerInterceptor implements ProducerInterceptor<String, User> {

   @Value("${kafka.users.author}")
   private String author;

   @Override
   public ProducerRecord<String, User> onSend(final ProducerRecord<String, User> producerRecord) {
      producerRecord.headers().add(generateLocalDateTimeHeader());
      producerRecord.headers().add(generateAuthorHeader());

      return producerRecord;
   }

   @Override
   public void onAcknowledgement(final RecordMetadata recordMetadata, final Exception e) {

   }

   @Override
   public void close() {

   }

   @Override
   public void configure(final Map<String, ?> map) {

   }

   private RecordHeader generateLocalDateTimeHeader() {
      LocalDateTime dateTime = LocalDateTime.now();
      long epochMilli = dateTime.toInstant(ZoneOffset.UTC)
                                .toEpochMilli();
      byte[] bytes = ByteBuffer.allocate(Long.BYTES)
                               .putLong(epochMilli)
                               .array();

      return new RecordHeader("date", bytes);
   }

   private RecordHeader generateAuthorHeader() {
      return new RecordHeader("author", author.getBytes(StandardCharsets.UTF_8));
   }
}
