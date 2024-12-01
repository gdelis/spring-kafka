package com.gdelis.spring.kafka.configuration;

import com.gdelis.spring.kafka.controller.ProductsHttpService;
import java.rmi.ServerException;
import java.time.LocalDateTime;
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
   public ProductsHttpService productsHttpService(final HttpServiceProxyFactory proxy) {
      return proxy.createClient(ProductsHttpService.class);
   }
   
   @Bean
   public HttpServiceProxyFactory httpServiceProxyFactory(@Value("${services.products.url}") final String baseUrl,
                                                          final RestClient.Builder builder) {
      RestClient client = builder.baseUrl(baseUrl)
                                 .defaultHeaders(headers -> {
                                    headers.add("X-Custom-Author", "george-delis");
                                    headers.add("X-Custom-Date",
                                                LocalDateTime.now()
                                                             .toString());
                                 })
                                 //.defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) -> {
                                 //   System.out.println("request.getURI() = " + request.getURI());
                                 //   System.out.println("response.getStatusText() = " + response.getStatusText());
                                 //
                                 //   throw new ClientException("client exception");
                                 //})
                                 .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                                    System.out.println("request.getURI() = " + request.getURI());
                                    System.out.println("response.getStatusText() = " + response.getStatusText());
                                    
                                    throw new ServerException("server exception");
                                 })
                                 .build();
      
      return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(client))
                                    .build();
   }
}
