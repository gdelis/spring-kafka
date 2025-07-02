package com.gdelis.spring.kafka.configuration.health;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.TopicDescription;
import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

/**
 * This registers a separate endpoint under the /actuator URI. This specific endpoint
 * will be accessed under /actuator/kafka-topics
 * <p>
 * This will register a separate-new endpoint under the /actuator URI
 */
@Component
@Endpoint(id = "kafka-topics")
public class KafkaManagementEndpoint {
   
   private final AdminClient kafkaAdminClient;
   private final Map<String, Topic> topicDetails;
   
   public KafkaManagementEndpoint(final AdminClient kafkaAdminClient) {
      this.kafkaAdminClient = kafkaAdminClient;
      this.topicDetails = new ConcurrentHashMap<>();
   }
   
   @ReadOperation
   public Set<String> getAllKafkaTopicsNames() {
      return this.getTopicNames();
   }
   
   @ReadOperation
   public Topic getTopicDetailsByName(@Selector final String name) {
      retrieveKafkaTopicsDetails();
      return topicDetails.get(name);
   }
   
   @WriteOperation
   public void configureTopic(@Selector final String name, final Topic feature) {
      topicDetails.put(name, feature);
   }
   
   @DeleteOperation
   public void deleteFeature(@Selector final String name) {
      topicDetails.remove(name);
   }
   
   private void retrieveKafkaTopicsDetails() {
      try {
         // Get detailed information for all topics
         Map<String, TopicDescription> topicDescriptions = kafkaAdminClient.describeTopics(this.getTopicNames())
                                                                           .allTopicNames()
                                                                           .get();
         
         // Update our topic map with fresh data from Kafka
         this.topicDetails.clear();
         this.topicDetails.putAll(topicDescriptions.entrySet()
                                                   .stream()
                                                   .collect(Collectors.toMap(Map.Entry::getKey,
                                                                             entry -> mapToTopic(entry.getValue()))));
      } catch (InterruptedException | ExecutionException e) {
         Thread.currentThread()
               .interrupt();
         throw new RuntimeException("Failed to retrieve Kafka topics", e);
      }
   }
   
   private Set<String> getTopicNames() {
      try {
         return kafkaAdminClient.listTopics()
                                .names()
                                .get();
      } catch (InterruptedException | ExecutionException e) {
         Thread.currentThread()
               .interrupt();
         throw new RuntimeException("Failed to retrieve Kafka topics", e);
      }
   }
   
   private Topic mapToTopic(final TopicDescription topicDescription) {
      Topic topic = new Topic();
      topic.setName(topicDescription.name());
      topic.setPartitions(topicDescription.partitions()
                                          .size());
      topic.setReplicationFactor(topicDescription.partitions()
                                                 .isEmpty() ? 0 : topicDescription.partitions()
                                                                                  .getFirst()
                                                                                  .replicas()
                                                                                  .size());
      topic.setEnabled(true); // You can set your own logic here
      return topic;
   }
   
   @Getter
   @Setter
   @AllArgsConstructor
   @NoArgsConstructor
   public static class Topic {
      private String name;
      private Integer partitions;
      private Integer replicationFactor;
      private Boolean enabled;
   }
}
