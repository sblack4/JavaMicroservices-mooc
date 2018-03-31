/*    */ package org.springframework.boot.loader.jar;
/*    */ 
/*    */ import java.io.EOFException;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.util.zip.Inflater;
/*    */ import java.util.zip.InflaterInputStream;
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
/*    */ class ZipInflaterInputStream
/*    */   extends InflaterInputStream
/*    */ {
/*    */   private boolean extraBytesWritten;
/*    */   private int available;
/*    */   
/*    */   ZipInflaterInputStream(InputStream inputStream, int size)
/*    */   {
/* 38 */     super(inputStream, new Inflater(true), getInflaterBufferSize(size));
/* 39 */     this.available = size;
/*    */   }
/*    */   
/*    */   public int available() throws IOException
/*    */   {
/* 44 */     if (this.available < 0) {
/* 45 */       return super.available();
/*    */     }
/* 47 */     return this.available;
/*    */   }
/*    */   
/*    */   public int read(byte[] b, int off, int len) throws IOException
/*    */   {
/* 52 */     int result = super.read(b, off, len);
/* 53 */     if (result != -1) {
/* 54 */       this.available -= result;
/*    */     }
/* 56 */     return result;
/*    */   }
/*    */   
/*    */   protected void fill() throws IOException
/*    */   {
/*    */     try {
/* 62 */       super.fill();
/*    */     }
/*    */     catch (EOFException ex) {
/* 65 */       if (this.extraBytesWritten) {
/* 66 */         throw ex;
/*    */       }
/* 68 */       this.len = 1;
/* 69 */       this.buf[0] = 0;
/* 70 */       this.extraBytesWritten = true;
/* 71 */       this.inf.setInput(this.buf, 0, this.len);
/*    */     }
/*    */   }
/*    */   
/*    */   private static int getInflaterBufferSize(long size) {
/* 76 */     size += 2L;
/* 77 */     size = size > 65536L ? 8192L : size;
/* 78 */     size = size <= 0L ? 4096L : size;
/* 79 */     return (int)size;
/*    */   }
/*    */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\jar\ZipInflaterInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */