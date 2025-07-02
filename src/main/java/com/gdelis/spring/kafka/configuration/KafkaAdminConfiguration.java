package com.gdelis.spring.kafka.configuration;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaAdminConfiguration {
   
   private final String bootstrapServers;
   
   public KafkaAdminConfiguration(@Value("${kafka.bootstrap-servers}") final String bootstrapServers) {
      this.bootstrapServers = bootstrapServers;
   }
   
   @Bean
   public AdminClient kafkaAdminClient() {
      
      Map<String, Object> configs = new HashMap<>();
      configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
      
      // Add any additional configuration as needed
      // configs.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
      // configs.put(AdminClientConfig.SASL_MECHANISM, "PLAIN");
      
      return AdminClient.create(configs);
   }
}
