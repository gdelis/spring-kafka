package com.gdelis.spring.kafka;

import com.gdelis.spring.kafka.utils.Telephone;
import jakarta.validation.constraints.Email;
import java.util.Map;
import lombok.Builder;

@Builder
public record UserDetails(String username,
                          String firstName,
                          String lastName,
                          @Email String email,
                          @Telephone String telephone,
                          UserTypeEnum type,
                          String postcode,
                          Map<String, String> details) {}