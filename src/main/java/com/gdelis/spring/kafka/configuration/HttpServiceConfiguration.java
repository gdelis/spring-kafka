package com.gdelis.spring.kafka.configuration;

import com.gdelis.spring.kafka.controller.OrdersHttpService;
import com.gdelis.spring.kafka.controller.ProductsHttpService;
import java.rmi.ServerException;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpServiceConfiguration {
   
   @Bean
   public ProductsHttpService productsHttpService(
       @Qualifier("productsHttpServiceProxyFactory") final HttpServiceProxyFactory proxy) {
      
      return proxy.createClient(ProductsHttpService.class);
   }
   
   @Bean
   public OrdersHttpService ordersHttpService(
       @Qualifier("productsHttpServiceProxyFactory") final HttpServiceProxyFactory proxy) {
      return proxy.createClient(OrdersHttpService.class);
   }
   
   @Bean
   public HttpServiceProxyFactory productsHttpServiceProxyFactory(
       @Value("${services.products.url}") final String baseUrl, final RestClient.Builder builder) {
      
      RestClient client = builder.baseUrl(baseUrl)
                                 .defaultHeaders(headers -> {
                                    headers.add("X-Custom-Author", "george-delis");
                                    headers.add("X-Custom-Date",
                                                LocalDateTime.now()
                                                             .toString());
                                 })
                                 .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                                    System.out.println("request.getURI() = " + request.getURI());
                                    System.out.println("response.getStatusText() = " + response.getStatusText());
                                    
                                    throw new ServerException("products - server exception");
                                 })
                                 .build();
      
      return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(client))
                                    .build();
   }
   
   @Bean
   public HttpServiceProxyFactory ordersHttpServiceProxyFactory(@Value("${services.orders.url}") final String baseUrl,
                                                                final RestClient.Builder builder) {
      
      RestClient client = builder.baseUrl(baseUrl)
                                 .defaultHeaders(headers -> {
                                    headers.add("X-Custom-Author", "george-delis");
                                    headers.add("X-Custom-Date",
                                                LocalDateTime.now()
                                                             .toString());
                                 })
                                 .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                                    System.out.println("request.getURI() = " + request.getURI());
                                    System.out.println("response.getStatusText() = " + response.getStatusText());
                                    
                                    throw new ServerException("orders - server exception");
                                 })
                                 .build();
      
      return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(client))
                                    .build();
   }
}
