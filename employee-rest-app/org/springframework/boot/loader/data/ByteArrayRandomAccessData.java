/*    */ package org.springframework.boot.loader.data;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
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
/*    */ public class ByteArrayRandomAccessData
/*    */   implements RandomAccessData
/*    */ {
/*    */   private final byte[] bytes;
/*    */   private final long offset;
/*    */   private final long length;
/*    */   
/*    */   public ByteArrayRandomAccessData(byte[] bytes)
/*    */   {
/* 36 */     this(bytes, 0L, bytes == null ? 0 : bytes.length);
/*    */   }
/*    */   
/*    */   public ByteArrayRandomAccessData(byte[] bytes, long offset, long length) {
/* 40 */     this.bytes = (bytes == null ? new byte[0] : bytes);
/* 41 */     this.offset = offset;
/* 42 */     this.length = length;
/*    */   }
/*    */   
/*    */   public InputStream getInputStream(RandomAccessData.ResourceAccess access)
/*    */   {
/* 47 */     return new ByteArrayInputStream(this.bytes, (int)this.offset, (int)this.length);
/*    */   }
/*    */   
/*    */   public RandomAccessData getSubsection(long offset, long length)
/*    */   {
/* 52 */     return new ByteArrayRandomAccessData(this.bytes, this.offset + offset, length);
/*    */   }
/*    */   
/*    */   public long getSize()
/*    */   {
/* 57 */     return this.length;
/*    */   }
/*    */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\data\ByteArrayRandomAccessData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */