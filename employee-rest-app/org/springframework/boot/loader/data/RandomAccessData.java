/*    */ package org.springframework.boot.loader.data;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
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
/*    */ public abstract interface RandomAccessData
/*    */ {
/*    */   public abstract InputStream getInputStream(ResourceAccess paramResourceAccess)
/*    */     throws IOException;
/*    */   
/*    */   public abstract RandomAccessData getSubsection(long paramLong1, long paramLong2);
/*    */   
/*    */   public abstract long getSize();
/*    */   
/*    */   public static enum ResourceAccess
/*    */   {
/* 62 */     ONCE, 
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 67 */     PER_READ;
/*    */     
/*    */     private ResourceAccess() {}
/*    */   }
/*    */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\data\RandomAccessData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */