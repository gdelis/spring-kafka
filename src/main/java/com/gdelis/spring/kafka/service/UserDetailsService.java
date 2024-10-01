package com.gdelis.spring.kafka.service;

import com.gdelis.spring.kafka.UserDetails;
import java.util.List;

public interface UserDetailsService {
   
   UserDetails createUserDetails(final UserDetails user);
   
   List<UserDetails> getAllUserDetails();
}
