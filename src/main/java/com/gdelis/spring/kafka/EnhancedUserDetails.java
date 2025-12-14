package com.gdelis.spring.kafka;

import com.gdelis.spring.kafka.utils.Telephone;
import jakarta.validation.constraints.Email;
import java.util.Map;
import lombok.Builder;

@Builder
public record EnhancedUserDetails(String username,
                                  String firstName,
                                  String lastName,
                                  @Email String email,
                                  @Telephone String telephone,
                                  UserTypeEnum type,
                                  String postcode,
                                  String address,
                                  Integer number,
                                  String flat,
                                  String building,
                                  CountryEnum country,
                                  Map<String, String> details) {}
