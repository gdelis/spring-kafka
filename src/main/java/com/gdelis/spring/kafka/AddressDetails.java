package com.gdelis.spring.kafka;

public record AddressDetails(String postcode,
                             String address,
                             Integer number,
                             String flat,
                             String building,
                             CountryEnum country) {}
