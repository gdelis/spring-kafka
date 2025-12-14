package com.gdelis.spring.kafka.domain;

import lombok.Builder;

@Builder
public record Order(Integer id,
                    String details) {}
