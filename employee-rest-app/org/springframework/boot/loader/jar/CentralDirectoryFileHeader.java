/*     */ package org.springframework.boot.loader.jar;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Calendar;
/*     */ import java.util.GregorianCalendar;
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
/*     */ final class CentralDirectoryFileHeader
/*     */   implements FileHeader
/*     */ {
/*  35 */   private static final AsciiBytes SLASH = new AsciiBytes("/");
/*     */   
/*  37 */   private static final byte[] NO_EXTRA = new byte[0];
/*     */   
/*  39 */   private static final AsciiBytes NO_COMMENT = new AsciiBytes("");
/*     */   
/*     */   private byte[] header;
/*     */   
/*     */   private int headerOffset;
/*     */   
/*     */   private AsciiBytes name;
/*     */   
/*     */   private byte[] extra;
/*     */   
/*     */   private AsciiBytes comment;
/*     */   
/*     */   private long localHeaderOffset;
/*     */   
/*     */ 
/*     */   CentralDirectoryFileHeader() {}
/*     */   
/*     */ 
/*     */   CentralDirectoryFileHeader(byte[] header, int headerOffset, AsciiBytes name, byte[] extra, AsciiBytes comment, long localHeaderOffset)
/*     */   {
/*  59 */     this.header = header;
/*  60 */     this.headerOffset = headerOffset;
/*  61 */     this.name = name;
/*  62 */     this.extra = extra;
/*  63 */     this.comment = comment;
/*  64 */     this.localHeaderOffset = localHeaderOffset;
/*     */   }
/*     */   
/*     */   void load(byte[] data, int dataOffset, RandomAccessData variableData, int variableOffset, JarEntryFilter filter)
/*     */     throws IOException
/*     */   {
/*  70 */     this.header = data;
/*  71 */     this.headerOffset = dataOffset;
/*  72 */     long nameLength = Bytes.littleEndianValue(data, dataOffset + 28, 2);
/*  73 */     long extraLength = Bytes.littleEndianValue(data, dataOffset + 30, 2);
/*  74 */     long commentLength = Bytes.littleEndianValue(data, dataOffset + 32, 2);
/*  75 */     this.localHeaderOffset = Bytes.littleEndianValue(data, dataOffset + 42, 4);
/*     */     
/*  77 */     dataOffset += 46;
/*  78 */     if (variableData != null) {
/*  79 */       data = Bytes.get(variableData.getSubsection(variableOffset + 46, nameLength + extraLength + commentLength));
/*     */       
/*  81 */       dataOffset = 0;
/*     */     }
/*  83 */     this.name = new AsciiBytes(data, dataOffset, (int)nameLength);
/*  84 */     if (filter != null) {
/*  85 */       this.name = filter.apply(this.name);
/*     */     }
/*  87 */     this.extra = NO_EXTRA;
/*  88 */     this.comment = NO_COMMENT;
/*  89 */     if (extraLength > 0L) {
/*  90 */       this.extra = new byte[(int)extraLength];
/*  91 */       System.arraycopy(data, (int)(dataOffset + nameLength), this.extra, 0, this.extra.length);
/*     */     }
/*     */     
/*  94 */     if (commentLength > 0L) {
/*  95 */       this.comment = new AsciiBytes(data, (int)(dataOffset + nameLength + extraLength), (int)commentLength);
/*     */     }
/*     */   }
/*     */   
/*     */   public AsciiBytes getName()
/*     */   {
/* 101 */     return this.name;
/*     */   }
/*     */   
/*     */   public boolean hasName(String name, String suffix)
/*     */   {
/* 106 */     return this.name.equals(new AsciiBytes(name + suffix));
/*     */   }
/*     */   
/*     */   public boolean isDirectory() {
/* 110 */     return this.name.endsWith(SLASH);
/*     */   }
/*     */   
/*     */   public int getMethod()
/*     */   {
/* 115 */     return (int)Bytes.littleEndianValue(this.header, this.headerOffset + 10, 2);
/*     */   }
/*     */   
/*     */   public long getTime() {
/* 119 */     long date = Bytes.littleEndianValue(this.header, this.headerOffset + 14, 2);
/* 120 */     long time = Bytes.littleEndianValue(this.header, this.headerOffset + 12, 2);
/* 121 */     return decodeMsDosFormatDateTime(date, time).getTimeInMillis();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Calendar decodeMsDosFormatDateTime(long date, long time)
/*     */   {
/* 133 */     int year = (int)(date >> 9 & 0x7F) + 1980;
/* 134 */     int month = (int)(date >> 5 & 0xF) - 1;
/* 135 */     int day = (int)(date & 0x1F);
/* 136 */     int hours = (int)(time >> 11 & 0x1F);
/* 137 */     int minutes = (int)(time >> 5 & 0x3F);
/* 138 */     int seconds = (int)(time << 1 & 0x3E);
/* 139 */     return new GregorianCalendar(year, month, day, hours, minutes, seconds);
/*     */   }
/*     */   
/*     */   public long getCrc() {
/* 143 */     return Bytes.littleEndianValue(this.header, this.headerOffset + 16, 4);
/*     */   }
/*     */   
/*     */   public long getCompressedSize()
/*     */   {
/* 148 */     return Bytes.littleEndianValue(this.header, this.headerOffset + 20, 4);
/*     */   }
/*     */   
/*     */   public long getSize()
/*     */   {
/* 153 */     return Bytes.littleEndianValue(this.header, this.headerOffset + 24, 4);
/*     */   }
/*     */   
/*     */   public byte[] getExtra() {
/* 157 */     return this.extra;
/*     */   }
/*     */   
/*     */   public AsciiBytes getComment() {
/* 161 */     return this.comment;
/*     */   }
/*     */   
/*     */   public long getLocalHeaderOffset()
/*     */   {
/* 166 */     return this.localHeaderOffset;
/*     */   }
/*     */   
/*     */   public CentralDirectoryFileHeader clone()
/*     */   {
/* 171 */     byte[] header = new byte[46];
/* 172 */     System.arraycopy(this.header, this.headerOffset, header, 0, header.length);
/* 173 */     return new CentralDirectoryFileHeader(header, 0, this.name, header, this.comment, this.localHeaderOffset);
/*     */   }
/*     */   
/*     */   public static CentralDirectoryFileHeader fromRandomAccessData(RandomAccessData data, int offset, JarEntryFilter filter)
/*     */     throws IOException
/*     */   {
/* 179 */     CentralDirectoryFileHeader fileHeader = new CentralDirectoryFileHeader();
/* 180 */     byte[] bytes = Bytes.get(data.getSubsection(offset, 46L));
/* 181 */     fileHeader.load(bytes, 0, data, offset, filter);
/* 182 */     return fileHeader;
/*     */   }
/*     */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\jar\CentralDirectoryFileHeader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */