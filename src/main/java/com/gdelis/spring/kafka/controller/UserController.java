package com.gdelis.spring.kafka.controller;

import com.gdelis.spring.kafka.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

   private final UserService userService;

   public UserController(final UserService userService) {
      this.userService = userService;
   }

   @PostMapping
   public void createUser(@RequestParam String name) {
      userService.sendUser(name);
   }
}

