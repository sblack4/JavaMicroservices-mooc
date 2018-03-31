/*    */ package com.example.rest;
/*    */ 
/*    */ import java.util.Optional;
/*    */ import java.util.Properties;
/*    */ import org.springframework.boot.SpringApplication;
/*    */ import org.springframework.boot.autoconfigure.SpringBootApplication;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @SpringBootApplication
/*    */ public class App
/*    */ {
/* 15 */   public static final Properties myProps = new Properties();
/*    */   
/*    */ 
/* 18 */   public static final Optional<String> host = Optional.ofNullable(System.getenv("HOSTNAME"));
/* 19 */   public static final Optional<String> port = Optional.ofNullable(System.getenv("PORT"));
/*    */   
/*    */ 
/*    */ 
/*    */   public static void main(String[] args)
/*    */   {
/* 25 */     myProps.setProperty("server.address", (String)host.orElse("localhost"));
/* 26 */     myProps.setProperty("server.port", (String)port.orElse("8080"));
/*    */     
/* 28 */     SpringApplication app = new SpringApplication(new Object[] { App.class });
/* 29 */     app.setDefaultProperties(myProps);
/* 30 */     app.run(args);
/*    */   }
/*    */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\BOOT-INF\classes\com\example\rest\App.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */