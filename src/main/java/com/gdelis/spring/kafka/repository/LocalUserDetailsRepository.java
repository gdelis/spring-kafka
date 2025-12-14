package com.gdelis.spring.kafka.repository;

import com.gdelis.spring.kafka.domain.UserDetails;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
public class LocalUserDetailsRepository implements UserDetailsRepository {
   
   private final List<UserDetails> users;
   
   public LocalUserDetailsRepository() {
      this.users = new ArrayList<>();
   }
   
   @Override
   public void save(final UserDetails user) {
      this.users.add(user);
   }
   
   @Override
   public List<UserDetails> findAll() {
      return users;
   }
}
