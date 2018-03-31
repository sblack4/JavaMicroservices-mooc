/*    */ package org.springframework.boot.loader.jar;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import org.springframework.boot.loader.data.RandomAccessData;
/*    */ import org.springframework.boot.loader.data.RandomAccessData.ResourceAccess;
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
/*    */ final class Bytes
/*    */ {
/* 32 */   private static final byte[] EMPTY_BYTES = new byte[0];
/*    */   
/*    */ 
/*    */   public static byte[] get(RandomAccessData data)
/*    */     throws IOException
/*    */   {
/* 38 */     InputStream inputStream = data.getInputStream(RandomAccessData.ResourceAccess.ONCE);
/*    */     try {
/* 40 */       return get(inputStream, data.getSize());
/*    */     }
/*    */     finally {
/* 43 */       inputStream.close();
/*    */     }
/*    */   }
/*    */   
/*    */   public static byte[] get(InputStream inputStream, long length) throws IOException {
/* 48 */     if (length == 0L) {
/* 49 */       return EMPTY_BYTES;
/*    */     }
/* 51 */     byte[] bytes = new byte[(int)length];
/* 52 */     if (!fill(inputStream, bytes)) {
/* 53 */       throw new IOException("Unable to read bytes");
/*    */     }
/* 55 */     return bytes;
/*    */   }
/*    */   
/*    */   public static boolean fill(InputStream inputStream, byte[] bytes) throws IOException {
/* 59 */     return fill(inputStream, bytes, 0, bytes.length);
/*    */   }
/*    */   
/*    */   private static boolean fill(InputStream inputStream, byte[] bytes, int offset, int length) throws IOException
/*    */   {
/* 64 */     while (length > 0) {
/* 65 */       int read = inputStream.read(bytes, offset, length);
/* 66 */       if (read == -1) {
/* 67 */         return false;
/*    */       }
/* 69 */       offset += read;
/* 70 */       length = -read;
/*    */     }
/* 72 */     return true;
/*    */   }
/*    */   
/*    */   public static long littleEndianValue(byte[] bytes, int offset, int length) {
/* 76 */     long value = 0L;
/* 77 */     for (int i = length - 1; i >= 0; i--) {
/* 78 */       value = value << 8 | bytes[(offset + i)] & 0xFF;
/*    */     }
/* 80 */     return value;
/*    */   }
/*    */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\jar\Bytes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */