package com.gdelis.spring.kafka.controller;

import com.gdelis.spring.kafka.UserDetails;
import com.gdelis.spring.kafka.repository.UserDetailsRepository;
import com.gdelis.spring.kafka.service.UserDetailsService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserDetailsController {

   private final UserDetailsService userDetailsService;
   private final UserDetailsRepository userDetailsRepository;

   public UserDetailsController(final UserDetailsService userDetailsService,
                                final UserDetailsRepository userDetailsRepository) {
      this.userDetailsService = userDetailsService;
      this.userDetailsRepository = userDetailsRepository;
   }

   @GetMapping
   public ResponseEntity<List<UserDetails>> getUserDetails() {
      return ResponseEntity.ok(userDetailsRepository.findAll());
   }

   @PostMapping
   public ResponseEntity<UserDetails> createUser(@RequestBody final UserDetails user) {
      return ResponseEntity.ok(userDetailsService.createUserDetails(user));
   }
}
