package com.gdelis.spring.kafka.interceptor;

import com.gdelis.spring.kafka.UserDetails;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.internals.RecordHeader;

public class AuthorHeaderProducerInterceptor implements ProducerInterceptor<String, UserDetails> {
   @Override
   public ProducerRecord<String, UserDetails> onSend(final ProducerRecord<String, UserDetails> producerRecord) {
      
      producerRecord.headers()
                    .add(new RecordHeader("author", "gdelis".getBytes(StandardCharsets.UTF_8)));
      
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
}
