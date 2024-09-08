package com.gdelis.spring.kafka.repository;

import com.gdelis.spring.kafka.UserDetails;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class LocalAdminDetailsRepository {

   private final List<UserDetails> admins;

   public LocalAdminDetailsRepository() {
      this.admins = new ArrayList<>();
   }

   public void save(final UserDetails userDetails) {
      this.admins.add(userDetails);
   }
}
