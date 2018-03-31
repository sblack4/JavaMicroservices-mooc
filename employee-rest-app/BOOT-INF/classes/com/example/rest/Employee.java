/*    */ package com.example.rest;
/*    */ 
/*    */ 
/*    */ public class Employee
/*    */ {
/*    */   private final long id;
/*    */   
/*    */   private final String firstName;
/*    */   private final String lastName;
/*    */   private final String email;
/*    */   private final String phone;
/*    */   private final String birthDate;
/*    */   private final String title;
/*    */   private final String dept;
/*    */   
/*    */   public Employee()
/*    */   {
/* 18 */     this.id = 0L;
/* 19 */     this.firstName = "";
/* 20 */     this.lastName = "";
/* 21 */     this.email = "";
/* 22 */     this.phone = "";
/* 23 */     this.birthDate = "";
/* 24 */     this.title = "";
/* 25 */     this.dept = "";
/*    */   }
/*    */   
/*    */   public Employee(long id, String firstName, String lastName, String email, String phone, String birthDate, String title, String dept) {
/* 29 */     this.id = id;
/* 30 */     this.firstName = firstName;
/* 31 */     this.lastName = lastName;
/* 32 */     this.email = email;
/* 33 */     this.phone = phone;
/* 34 */     this.birthDate = birthDate;
/* 35 */     this.title = title;
/* 36 */     this.dept = dept;
/*    */   }
/*    */   
/*    */   public long getId() {
/* 40 */     return this.id;
/*    */   }
/*    */   
/*    */   public String getFirstName() {
/* 44 */     return this.firstName;
/*    */   }
/*    */   
/*    */   public String getLastName() {
/* 48 */     return this.lastName;
/*    */   }
/*    */   
/*    */   public String getEmail() {
/* 52 */     return this.email;
/*    */   }
/*    */   
/*    */   public String getPhone() {
/* 56 */     return this.phone;
/*    */   }
/*    */   
/*    */   public String getBirthDate() {
/* 60 */     return this.birthDate;
/*    */   }
/*    */   
/*    */   public String getTitle() {
/* 64 */     return this.title;
/*    */   }
/*    */   
/*    */   public String getDept() {
/* 68 */     return this.dept;
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 73 */     return "ID: " + this.id + " First Name: " + this.firstName + " Last Name: " + this.lastName + " EMail: " + this.email + " Phone: " + this.phone + " Birth Date: " + this.birthDate + " Title: " + this.title + " Department: " + this.dept;
/*    */   }
/*    */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\BOOT-INF\classes\com\example\rest\Employee.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */