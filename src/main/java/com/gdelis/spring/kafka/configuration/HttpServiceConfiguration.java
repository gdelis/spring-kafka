package com.gdelis.spring.kafka.configuration;

import com.gdelis.spring.kafka.controller.ProductsHttpService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpServiceConfiguration {
   
   @Bean
   public ProductsHttpService productsHttpService(final HttpServiceProxyFactory proxy) {
      return proxy.createClient(ProductsHttpService.class);
   }
   
   @Bean
   public HttpServiceProxyFactory httpServiceProxyFactory(final RestClient.Builder builder) {
      RestClient client = builder.baseUrl("http://localhost:9000")
                                 .build();
      
      return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(client))
                                    .build();
   }
}
