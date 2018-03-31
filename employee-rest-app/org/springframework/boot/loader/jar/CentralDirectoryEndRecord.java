/*     */ package org.springframework.boot.loader.jar;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.springframework.boot.loader.data.RandomAccessData;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class CentralDirectoryEndRecord
/*     */ {
/*     */   private static final int MINIMUM_SIZE = 22;
/*     */   private static final int MAXIMUM_COMMENT_LENGTH = 65535;
/*     */   private static final int MAXIMUM_SIZE = 65557;
/*     */   private static final int SIGNATURE = 101010256;
/*     */   private static final int COMMENT_LENGTH_OFFSET = 20;
/*     */   private static final int READ_BLOCK_SIZE = 256;
/*     */   private byte[] block;
/*     */   private int offset;
/*     */   private int size;
/*     */   
/*     */   CentralDirectoryEndRecord(RandomAccessData data)
/*     */     throws IOException
/*     */   {
/*  57 */     this.block = createBlockFromEndOfData(data, 256);
/*  58 */     this.size = 22;
/*  59 */     this.offset = (this.block.length - this.size);
/*  60 */     while (!isValid()) {
/*  61 */       this.size += 1;
/*  62 */       if (this.size > this.block.length) {
/*  63 */         if ((this.size >= 65557) || (this.size > data.getSize())) {
/*  64 */           throw new IOException("Unable to find ZIP central directory records after reading " + this.size + " bytes");
/*     */         }
/*     */         
/*  67 */         this.block = createBlockFromEndOfData(data, this.size + 256);
/*     */       }
/*  69 */       this.offset = (this.block.length - this.size);
/*     */     }
/*     */   }
/*     */   
/*     */   private byte[] createBlockFromEndOfData(RandomAccessData data, int size) throws IOException
/*     */   {
/*  75 */     int length = (int)Math.min(data.getSize(), size);
/*  76 */     return Bytes.get(data.getSubsection(data.getSize() - length, length));
/*     */   }
/*     */   
/*     */   private boolean isValid() {
/*  80 */     if ((this.block.length < 22) || 
/*  81 */       (Bytes.littleEndianValue(this.block, this.offset + 0, 4) != 101010256L)) {
/*  82 */       return false;
/*     */     }
/*     */     
/*  85 */     long commentLength = Bytes.littleEndianValue(this.block, this.offset + 20, 2);
/*     */     
/*  87 */     return this.size == 22L + commentLength;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getStartOfArchive(RandomAccessData data)
/*     */   {
/*  98 */     long length = Bytes.littleEndianValue(this.block, this.offset + 12, 4);
/*  99 */     long specifiedOffset = Bytes.littleEndianValue(this.block, this.offset + 16, 4);
/* 100 */     long actualOffset = data.getSize() - this.size - length;
/* 101 */     return actualOffset - specifiedOffset;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public RandomAccessData getCentralDirectory(RandomAccessData data)
/*     */   {
/* 111 */     long offset = Bytes.littleEndianValue(this.block, this.offset + 16, 4);
/* 112 */     long length = Bytes.littleEndianValue(this.block, this.offset + 12, 4);
/* 113 */     return data.getSubsection(offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getNumberOfRecords()
/*     */   {
/* 121 */     return (int)Bytes.littleEndianValue(this.block, this.offset + 10, 2);
/*     */   }
/*     */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\jar\CentralDirectoryEndRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */