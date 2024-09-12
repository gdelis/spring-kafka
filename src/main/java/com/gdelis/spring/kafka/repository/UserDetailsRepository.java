package com.gdelis.spring.kafka.repository;

import com.gdelis.spring.kafka.UserDetails;
import java.util.List;

public interface UserDetailsRepository {

   void save(final UserDetails user);
   List<UserDetails> findAll();
}
