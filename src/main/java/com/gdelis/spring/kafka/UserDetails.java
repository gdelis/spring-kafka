package com.gdelis.spring.kafka;

import com.gdelis.spring.kafka.utils.Telephone;
import jakarta.validation.constraints.Email;
import java.util.Map;

public record UserDetails(String username, String firstName, String lastName, @Email String email,
                          @Telephone String telephone, CountryEnum country, UserTypeEnum type,
                          Map<String, String> details) {}