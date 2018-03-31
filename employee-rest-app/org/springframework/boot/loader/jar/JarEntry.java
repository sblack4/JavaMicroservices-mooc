/*     */ package org.springframework.boot.loader.jar;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.security.CodeSigner;
/*     */ import java.security.cert.Certificate;
/*     */ import java.util.jar.Attributes;
/*     */ import java.util.jar.Manifest;
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
/*     */ class JarEntry
/*     */   extends java.util.jar.JarEntry
/*     */   implements FileHeader
/*     */ {
/*     */   private Certificate[] certificates;
/*     */   private CodeSigner[] codeSigners;
/*     */   private final JarFile jarFile;
/*     */   private long localHeaderOffset;
/*     */   
/*     */   JarEntry(JarFile jarFile, CentralDirectoryFileHeader header)
/*     */   {
/*  43 */     super(header.getName().toString());
/*  44 */     this.jarFile = jarFile;
/*  45 */     this.localHeaderOffset = header.getLocalHeaderOffset();
/*  46 */     setCompressedSize(header.getCompressedSize());
/*  47 */     setMethod(header.getMethod());
/*  48 */     setCrc(header.getCrc());
/*  49 */     setSize(header.getSize());
/*  50 */     setExtra(header.getExtra());
/*  51 */     setComment(header.getComment().toString());
/*  52 */     setSize(header.getSize());
/*  53 */     setTime(header.getTime());
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean hasName(String name, String suffix)
/*     */   {
/*  59 */     return (getName().length() == name.length() + suffix.length()) && (getName().startsWith(name)) && (getName().endsWith(suffix));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   URL getUrl()
/*     */     throws MalformedURLException
/*     */   {
/*  68 */     return new URL(this.jarFile.getUrl(), getName());
/*     */   }
/*     */   
/*     */   public Attributes getAttributes() throws IOException
/*     */   {
/*  73 */     Manifest manifest = this.jarFile.getManifest();
/*  74 */     return manifest == null ? null : manifest.getAttributes(getName());
/*     */   }
/*     */   
/*     */   public Certificate[] getCertificates()
/*     */   {
/*  79 */     if ((this.jarFile.isSigned()) && (this.certificates == null)) {
/*  80 */       this.jarFile.setupEntryCertificates(this);
/*     */     }
/*  82 */     return this.certificates;
/*     */   }
/*     */   
/*     */   public CodeSigner[] getCodeSigners()
/*     */   {
/*  87 */     if ((this.jarFile.isSigned()) && (this.codeSigners == null)) {
/*  88 */       this.jarFile.setupEntryCertificates(this);
/*     */     }
/*  90 */     return this.codeSigners;
/*     */   }
/*     */   
/*     */   void setCertificates(java.util.jar.JarEntry entry) {
/*  94 */     this.certificates = entry.getCertificates();
/*  95 */     this.codeSigners = entry.getCodeSigners();
/*     */   }
/*     */   
/*     */   public long getLocalHeaderOffset()
/*     */   {
/* 100 */     return this.localHeaderOffset;
/*     */   }
/*     */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\jar\JarEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */