package com.gdelis.spring.kafka;

import lombok.Builder;

@Builder
public record AddressDetails(String postcode,
                             String address,
                             Integer number,
                             String flat,
                             String building,
                             CountryEnum country) {}
