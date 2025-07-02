package com.gdelis.spring.kafka.configuration.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * This is registered under the /actuator/health endpoint. Thus, we can
 * add it as part of a health group
 */
@Component("kafka-consumer")
public class KafkaConsumerHealthIndicator implements HealthIndicator {
   
   @Override
   public Health health() {
      return Health.up()
                   .build();
   }
   
   @Override
   public Health getHealth(final boolean includeDetails) {
      return HealthIndicator.super.getHealth(includeDetails);
   }
}
