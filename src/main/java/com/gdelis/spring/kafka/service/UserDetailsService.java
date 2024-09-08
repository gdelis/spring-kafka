package com.gdelis.spring.kafka.service;

import com.gdelis.spring.kafka.UserDetails;

public interface UserDetailsService {

   UserDetails createUserDetails(final UserDetails user);
}
