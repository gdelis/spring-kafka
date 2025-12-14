package com.gdelis.spring.kafka.configuration;

import com.gdelis.spring.kafka.UserDetails;
import com.gdelis.spring.kafka.UserTypeEnum;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import java.util.Collections;
import java.util.Properties;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
//@EnableKafkaStreams
public class KafkaStreamsConfiguration {
   
   @Bean
   public AdminClient adminClient(Properties kafkaStreamsProperties) {
      // Reuse the existing streams properties to build AdminClient
      return AdminClient.create(kafkaStreamsProperties);
   }
   
   @Bean
   public CommandLineRunner ensureSourceTopicExists(AdminClient adminClient,
                                                    @Value("${kafka.users.topic.source.name}") String sourceTopicName,
                                                    @Value("${kafka.users.topic.partitions}") int partitions) {
      return args -> {
         try {
            var existingTopics = adminClient.listTopics().names().get();
            if (!existingTopics.contains(sourceTopicName)) {
               NewTopic newTopic = new NewTopic(sourceTopicName, partitions, (short) 1);
               adminClient.createTopics(Collections.singleton(newTopic)).all().get();
               System.out.println("Created missing source topic: " + sourceTopicName);
            } else {
               System.out.println("Source topic already exists: " + sourceTopicName);
            }
         } catch (Exception e) {
            throw new RuntimeException("Failed to ensure source topic exists: " + sourceTopicName, e);
         }
      };
   }
   
   @Bean
   Properties kafkaStreamsProperties() {
      Properties props = new Properties();
      
      props.put(StreamsConfig.APPLICATION_ID_CONFIG, "enhanced-users-details-stream");
      props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
      props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
      props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class.getName());
      // Enable Kafka Streams embedded optimization modem - disabled by default:
      //props.put(StreamsConfig.TOPOLOGY_OPTIMIZATION_CONFIG, StreamsConfig.OPTIMIZE);
      props.put("schema.registry.url", "http://localhost:8081");
      
      return props;
   }
   
   @Bean
   @DependsOn({"usersSource", "usersSink"})
   Topology enhancedUserDetailsTopology(@Value("${kafka.users.topic.source.name}") final String usersTopicName,
                                        @Value("${kafka.users.topic.sink.name}") final String enhancedUsersTopicName) {
      StreamsBuilder builder = new StreamsBuilder();
      
      KStream<String, GenericRecord> userDetailsStream =
          builder.stream(usersTopicName, Consumed.with(new Serdes.StringSerde(), new GenericAvroSerde()));
      
      userDetailsStream.mapValues(s -> {
         UserDetails.UserDetailsBuilder userDetailsBuilder = UserDetails.builder();
         
         userDetailsBuilder.username(s.get("username")
                                      .toString());
         userDetailsBuilder.firstName(s.get("firstName")
                                       .toString());
         userDetailsBuilder.lastName(s.get("lastName")
                                      .toString());
         userDetailsBuilder.email(s.get("email")
                                   .toString());
         userDetailsBuilder.telephone(s.get("telephone")
                                       .toString());
         userDetailsBuilder.postcode(s.get("postcode")
                                      .toString());
         userDetailsBuilder.type(s.get("type") != null ? Enum.valueOf(UserTypeEnum.class,
                                                                      s.get("type")
                                                                       .toString()) : UserTypeEnum.USER);
         
         return userDetailsBuilder.build();
      });
      
      userDetailsStream.peek((k, v) -> System.out.println("k = " + k + ", v = " + v));
      
      userDetailsStream.to(enhancedUsersTopicName, Produced.with(new Serdes.StringSerde(), new GenericAvroSerde()));
      
      return builder.build();
   }
   
   @Bean
   @DependsOn("enhancedUserDetailsTopology")
   KafkaStreams enhancedUserDetailsKafkaStream(@Qualifier("kafkaStreamsProperties") final Properties properties,
                                               @Qualifier("enhancedUserDetailsTopology") final Topology topology) {
      
      // Build the topology of the kafka streams:
      KafkaStreams streams = new KafkaStreams(topology, properties);
      
      // Start streaming:
      streams.start();
      
      System.out.println("Kafka Streams app started.");
      
      // Close Kafka Streams when JVM shuts down:
      //Runtime.getRuntime()
      //       .addShutdownHook(new Thread(streams::close));
      
      //System.out.println("Kafka Streams app terminated.");
      
      return streams;
   }
}
