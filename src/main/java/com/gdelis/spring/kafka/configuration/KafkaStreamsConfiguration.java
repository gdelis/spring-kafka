package com.gdelis.spring.kafka.configuration;

import com.gdelis.spring.kafka.UserDetails;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import java.util.Properties;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaStreamsConfiguration {
   
   @Bean
   Properties userKafkaStreamsProperties() {
      Properties props = new Properties();
      
      props.put(StreamsConfig.APPLICATION_ID_CONFIG, "user-details-processor");
      props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
      props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
      props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class.getName());
      props.put("schema.registry.url", "http://localhost:8081");
      
      return props;
   }
   
   @Bean
   ValueMapper<Object, Object> userDetailsValueMapper(@Value("${kafka.users.topic.source.name}") final String source) {
      return userDetails -> {
         ((UserDetails) userDetails).details()
                                    .put("after-processing", "usersTopic name: " + source);
         
         return userDetails;
      };
   }
   
   // It is a good practice to externalise the topology of a kafka streams:
   @Bean
   Topology userDetailsKafkaStreamsTopology(@Value("${kafka.users.topic.source.name}") final String source,
                                            @Value("${kafka.users.topic.sink.name}") final String sink,
                                            @Qualifier("userDetailsValueMapper")
                                            final ValueMapper<Object, Object> userDetailsValueMapper) {
      // This is a DSL approach:
      StreamsBuilder builder = new StreamsBuilder();
      
      // Define the topology of the Kafka Streams:
      KStream<Object, Object> stream = builder.stream(source);
      stream.mapValues(userDetailsValueMapper);
      stream.to(sink);
      
      return builder.build();
   }
   
   @Bean
   KafkaStreams usersKafkaStreams(@Qualifier("userKafkaStreamsProperties") final Properties userKafkaStreamsProperties,
                                  @Qualifier("userDetailsKafkaStreamsTopology") final Topology topology) {
      
      // Build the topology of the kafka streams:
      KafkaStreams streams = new KafkaStreams(topology, userKafkaStreamsProperties);
      
      // Start streaming:
      streams.start();
      
      // Close Kafka Streams when JVM shuts down:
      Runtime.getRuntime()
             .addShutdownHook(new Thread(streams::close));
      
      return streams;
   }
}
