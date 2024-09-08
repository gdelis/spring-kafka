package com.gdelis.spring.kafka.repository;

import com.gdelis.spring.kafka.UserDetails;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class LocalUserDetailsRepository {

   private final List<UserDetails> users;

   public LocalUserDetailsRepository() {
      this.users = new ArrayList<>();
   }

   public void save(final UserDetails user) {
      this.users.add(user);
   }
}
