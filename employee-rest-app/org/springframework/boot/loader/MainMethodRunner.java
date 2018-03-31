/*    */ package org.springframework.boot.loader;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MainMethodRunner
/*    */ {
/*    */   private final String mainClassName;
/*    */   private final String[] args;
/*    */   
/*    */   public MainMethodRunner(String mainClass, String[] args)
/*    */   {
/* 40 */     this.mainClassName = mainClass;
/* 41 */     this.args = (args == null ? null : (String[])args.clone());
/*    */   }
/*    */   
/*    */   public void run() throws Exception
/*    */   {
/* 46 */     Class<?> mainClass = Thread.currentThread().getContextClassLoader().loadClass(this.mainClassName);
/* 47 */     Method mainMethod = mainClass.getDeclaredMethod("main", new Class[] { String[].class });
/* 48 */     mainMethod.invoke(null, new Object[] { this.args });
/*    */   }
/*    */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\MainMethodRunner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */