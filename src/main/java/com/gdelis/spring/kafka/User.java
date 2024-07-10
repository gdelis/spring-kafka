package com.gdelis.spring.kafka;

public class User {
   String firstName;
   String lastName;
   String telephone;

   public User() {
   }

   public User(final String firstName, final String lastName, final String telephone) {
      this.firstName = firstName;
      this.lastName = lastName;
      this.telephone = telephone;
   }

   public String getFirstName() {
      return firstName;
   }

   public void setFirstName(final String firstName) {
      this.firstName = firstName;
   }

   public String getLastName() {
      return lastName;
   }

   public void setLastName(final String lastName) {
      this.lastName = lastName;
   }

   public String getTelephone() {
      return telephone;
   }

   public void setTelephone(final String telephone) {
      this.telephone = telephone;
   }
}
