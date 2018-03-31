/*     */ package org.springframework.boot.loader.jar;
/*     */ 
/*     */ import java.nio.charset.Charset;
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
/*     */ final class AsciiBytes
/*     */ {
/*  29 */   private static final Charset UTF_8 = Charset.forName("UTF-8");
/*     */   
/*     */ 
/*     */   private final byte[] bytes;
/*     */   
/*     */ 
/*     */   private final int offset;
/*     */   
/*     */   private final int length;
/*     */   
/*     */   private String string;
/*     */   
/*     */   private int hash;
/*     */   
/*     */ 
/*     */   AsciiBytes(String string)
/*     */   {
/*  46 */     this(string.getBytes(UTF_8));
/*  47 */     this.string = string;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   AsciiBytes(byte[] bytes)
/*     */   {
/*  56 */     this(bytes, 0, bytes.length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   AsciiBytes(byte[] bytes, int offset, int length)
/*     */   {
/*  67 */     if ((offset < 0) || (length < 0) || (offset + length > bytes.length)) {
/*  68 */       throw new IndexOutOfBoundsException();
/*     */     }
/*  70 */     this.bytes = bytes;
/*  71 */     this.offset = offset;
/*  72 */     this.length = length;
/*     */   }
/*     */   
/*     */   public int length() {
/*  76 */     return this.length;
/*     */   }
/*     */   
/*     */   public boolean startsWith(AsciiBytes prefix) {
/*  80 */     if (this == prefix) {
/*  81 */       return true;
/*     */     }
/*  83 */     if (prefix.length > this.length) {
/*  84 */       return false;
/*     */     }
/*  86 */     for (int i = 0; i < prefix.length; i++) {
/*  87 */       if (this.bytes[(i + this.offset)] != prefix.bytes[(i + prefix.offset)]) {
/*  88 */         return false;
/*     */       }
/*     */     }
/*  91 */     return true;
/*     */   }
/*     */   
/*     */   public boolean endsWith(AsciiBytes postfix) {
/*  95 */     if (this == postfix) {
/*  96 */       return true;
/*     */     }
/*  98 */     if (postfix.length > this.length) {
/*  99 */       return false;
/*     */     }
/* 101 */     for (int i = 0; i < postfix.length; i++) {
/* 102 */       if (this.bytes[(this.offset + (this.length - 1) - i)] != postfix.bytes[(postfix.offset + (postfix.length - 1) - i)])
/*     */       {
/* 104 */         return false;
/*     */       }
/*     */     }
/* 107 */     return true;
/*     */   }
/*     */   
/*     */   public AsciiBytes substring(int beginIndex) {
/* 111 */     return substring(beginIndex, this.length);
/*     */   }
/*     */   
/*     */   public AsciiBytes substring(int beginIndex, int endIndex) {
/* 115 */     int length = endIndex - beginIndex;
/* 116 */     if (this.offset + length > this.bytes.length) {
/* 117 */       throw new IndexOutOfBoundsException();
/*     */     }
/* 119 */     return new AsciiBytes(this.bytes, this.offset + beginIndex, length);
/*     */   }
/*     */   
/*     */   public AsciiBytes append(String string) {
/* 123 */     if ((string == null) || (string.length() == 0)) {
/* 124 */       return this;
/*     */     }
/* 126 */     return append(string.getBytes(UTF_8));
/*     */   }
/*     */   
/*     */   public AsciiBytes append(AsciiBytes asciiBytes) {
/* 130 */     if ((asciiBytes == null) || (asciiBytes.length() == 0)) {
/* 131 */       return this;
/*     */     }
/* 133 */     return append(asciiBytes.bytes);
/*     */   }
/*     */   
/*     */   public AsciiBytes append(byte[] bytes) {
/* 137 */     if ((bytes == null) || (bytes.length == 0)) {
/* 138 */       return this;
/*     */     }
/* 140 */     byte[] combined = new byte[this.length + bytes.length];
/* 141 */     System.arraycopy(this.bytes, this.offset, combined, 0, this.length);
/* 142 */     System.arraycopy(bytes, 0, combined, this.length, bytes.length);
/* 143 */     return new AsciiBytes(combined);
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 148 */     if (this.string == null) {
/* 149 */       this.string = new String(this.bytes, this.offset, this.length, UTF_8);
/*     */     }
/* 151 */     return this.string;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 156 */     int hash = this.hash;
/* 157 */     if ((hash == 0) && (this.bytes.length > 0)) {
/* 158 */       for (int i = this.offset; i < this.offset + this.length; i++) {
/* 159 */         int b = this.bytes[i] & 0xFF;
/* 160 */         if (b > 127)
/*     */         {
/* 162 */           for (int size = 0; size < 3; size++) {
/* 163 */             if ((b & 64 >> size) == 0) {
/* 164 */               b &= 31 >> size;
/* 165 */               for (int j = 0; j < size; j++) {
/* 166 */                 b <<= 6;
/* 167 */                 b |= this.bytes[(++i)] & 0x3F;
/*     */               }
/* 169 */               break;
/*     */             }
/*     */           }
/*     */         }
/* 173 */         hash = 31 * hash + b;
/*     */       }
/* 175 */       this.hash = hash;
/*     */     }
/* 177 */     return hash;
/*     */   }
/*     */   
/*     */   public boolean equals(Object obj)
/*     */   {
/* 182 */     if (obj == null) {
/* 183 */       return false;
/*     */     }
/* 185 */     if (this == obj) {
/* 186 */       return true;
/*     */     }
/* 188 */     if (obj.getClass().equals(AsciiBytes.class)) {
/* 189 */       AsciiBytes other = (AsciiBytes)obj;
/* 190 */       if (this.length == other.length) {
/* 191 */         for (int i = 0; i < this.length; i++) {
/* 192 */           if (this.bytes[(this.offset + i)] != other.bytes[(other.offset + i)]) {
/* 193 */             return false;
/*     */           }
/*     */         }
/* 196 */         return true;
/*     */       }
/*     */     }
/* 199 */     return false;
/*     */   }
/*     */   
/*     */   static String toString(byte[] bytes) {
/* 203 */     return new String(bytes, UTF_8);
/*     */   }
/*     */   
/*     */   public static int hashCode(String string)
/*     */   {
/* 208 */     return string.hashCode();
/*     */   }
/*     */   
/*     */   public static int hashCode(int hash, String string) {
/* 212 */     char[] chars = string.toCharArray();
/* 213 */     for (int i = 0; i < chars.length; i++) {
/* 214 */       hash = 31 * hash + chars[i];
/*     */     }
/* 216 */     return hash;
/*     */   }
/*     */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\jar\AsciiBytes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */