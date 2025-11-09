package com.gdelis.spring.kafka.configuration.connector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.connect.util.ConnectorUtils;

public class CustomSourceConnector extends SourceConnector {
   
   private static final ConfigDef CONFIG_DEF =
       new ConfigDef().define("url", ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "The url to fetch data from")
                      .define("topic", ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "The topic to publish data");
   
   @Override
   public void start(final Map<String, String> props) {
   
   }
   
   @Override
   public Class<? extends Task> taskClass() {
      return null;
   }
   
   @Override
   public List<Map<String, String>> taskConfigs(final int maxTasks) {
      List<Map<String, String>> taskConfigs = new ArrayList<>();
      List<String> symbols = List.of();
      
      ConnectorUtils.groupPartitions(symbols, maxTasks);
      
      return List.of();
   }
   
   @Override
   public void stop() {
   
   }
   
   @Override
   public ConfigDef config() {
      return CONFIG_DEF;
   }
   
   @Override
   public String version() {
      return "";
   }
}
