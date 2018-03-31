/*     */ package org.springframework.boot.loader.jar;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ class CentralDirectoryParser
/*     */ {
/*  33 */   private int CENTRAL_DIRECTORY_HEADER_BASE_SIZE = 46;
/*     */   
/*  35 */   private final List<CentralDirectoryVisitor> visitors = new ArrayList();
/*     */   
/*     */   public <T extends CentralDirectoryVisitor> T addVisitor(T visitor) {
/*  38 */     this.visitors.add(visitor);
/*  39 */     return visitor;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public RandomAccessData parse(RandomAccessData data, boolean skipPrefixBytes)
/*     */     throws IOException
/*     */   {
/*  51 */     CentralDirectoryEndRecord endRecord = new CentralDirectoryEndRecord(data);
/*  52 */     if (skipPrefixBytes) {
/*  53 */       data = getArchiveData(endRecord, data);
/*     */     }
/*  55 */     RandomAccessData centralDirectoryData = endRecord.getCentralDirectory(data);
/*  56 */     visitStart(endRecord, centralDirectoryData);
/*  57 */     parseEntries(endRecord, centralDirectoryData);
/*  58 */     visitEnd();
/*  59 */     return data;
/*     */   }
/*     */   
/*     */   private void parseEntries(CentralDirectoryEndRecord endRecord, RandomAccessData centralDirectoryData) throws IOException
/*     */   {
/*  64 */     byte[] bytes = Bytes.get(centralDirectoryData);
/*  65 */     CentralDirectoryFileHeader fileHeader = new CentralDirectoryFileHeader();
/*  66 */     int dataOffset = 0;
/*  67 */     for (int i = 0; i < endRecord.getNumberOfRecords(); i++) {
/*  68 */       fileHeader.load(bytes, dataOffset, null, 0, null);
/*  69 */       visitFileHeader(dataOffset, fileHeader);
/*     */       
/*     */ 
/*  72 */       dataOffset = dataOffset + (this.CENTRAL_DIRECTORY_HEADER_BASE_SIZE + fileHeader.getName().length() + fileHeader.getComment().length() + fileHeader.getExtra().length);
/*     */     }
/*     */   }
/*     */   
/*     */   private RandomAccessData getArchiveData(CentralDirectoryEndRecord endRecord, RandomAccessData data)
/*     */   {
/*  78 */     long offset = endRecord.getStartOfArchive(data);
/*  79 */     if (offset == 0L) {
/*  80 */       return data;
/*     */     }
/*  82 */     return data.getSubsection(offset, data.getSize() - offset);
/*     */   }
/*     */   
/*     */   private void visitStart(CentralDirectoryEndRecord endRecord, RandomAccessData centralDirectoryData)
/*     */   {
/*  87 */     for (CentralDirectoryVisitor visitor : this.visitors) {
/*  88 */       visitor.visitStart(endRecord, centralDirectoryData);
/*     */     }
/*     */   }
/*     */   
/*     */   private void visitFileHeader(int dataOffset, CentralDirectoryFileHeader fileHeader) {
/*  93 */     for (CentralDirectoryVisitor visitor : this.visitors) {
/*  94 */       visitor.visitFileHeader(fileHeader, dataOffset);
/*     */     }
/*     */   }
/*     */   
/*     */   private void visitEnd() {
/*  99 */     for (CentralDirectoryVisitor visitor : this.visitors) {
/* 100 */       visitor.visitEnd();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\jar\CentralDirectoryParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */