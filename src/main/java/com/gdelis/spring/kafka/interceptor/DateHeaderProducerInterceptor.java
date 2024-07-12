package com.gdelis.spring.kafka.interceptor;

import com.gdelis.spring.kafka.UserDetails;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.internals.RecordHeader;

// Each interceptor can add only 1 header...
public class DateHeaderProducerInterceptor implements ProducerInterceptor<String, UserDetails> {

   @Override
   public ProducerRecord<String, UserDetails> onSend(final ProducerRecord<String, UserDetails> producerRecord) {
      producerRecord.headers()
                    .add(generateLocalDateTimeHeader());

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
      byte[] bytes = LocalDateTime.now()
                                  .toString()
                                  .getBytes(StandardCharsets.UTF_8);

      return new RecordHeader("date", bytes);
   }
}
