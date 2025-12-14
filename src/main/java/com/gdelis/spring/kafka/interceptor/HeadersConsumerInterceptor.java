package com.gdelis.spring.kafka.interceptor;

import com.gdelis.spring.kafka.domain.UserDetails;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

public class HeadersConsumerInterceptor implements ConsumerInterceptor<String, UserDetails> {
   
   @Override
   public ConsumerRecords<String, UserDetails> onConsume(final ConsumerRecords<String, UserDetails> consumerRecords) {
      
      consumerRecords.forEach(record -> {
         record.headers()
               .forEach(header -> {
                  System.out.println("header = " + header);
               });
      });
      
      return consumerRecords;
   }
   
   @Override
   public void onCommit(final Map<TopicPartition, OffsetAndMetadata> map) {
   
   }
   
   @Override
   public void close() {
   
   }
   
   @Override
   public void configure(final Map<String, ?> map) {
   
   }
}
