package com.gdelis.spring.kafka.controller;

import com.gdelis.spring.kafka.UserDetails;
import com.gdelis.spring.kafka.service.UserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserDetailsController {

   private final UserDetailsService userDetailsService;

   public UserDetailsController(final UserDetailsService userDetailsService) {
      this.userDetailsService = userDetailsService;
   }

   @PostMapping
   public ResponseEntity<UserDetails> createUser(@RequestBody final UserDetails user) {
      return ResponseEntity.ok(userDetailsService.createUserDetails(user));
   }
}
