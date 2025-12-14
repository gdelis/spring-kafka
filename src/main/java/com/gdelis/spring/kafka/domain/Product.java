package com.gdelis.spring.kafka.domain;

import lombok.Builder;

@Builder
public record Product(Integer id,
                      String name) {}
