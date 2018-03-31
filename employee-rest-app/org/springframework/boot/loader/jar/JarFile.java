/*     */ package org.springframework.boot.loader.jar;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.jar.JarInputStream;
/*     */ import java.util.jar.Manifest;
/*     */ import java.util.zip.ZipEntry;
/*     */ import org.springframework.boot.loader.data.RandomAccessData;
/*     */ import org.springframework.boot.loader.data.RandomAccessData.ResourceAccess;
/*     */ import org.springframework.boot.loader.data.RandomAccessDataFile;
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
/*     */ public class JarFile
/*     */   extends java.util.jar.JarFile
/*     */ {
/*     */   private static final String MANIFEST_NAME = "META-INF/MANIFEST.MF";
/*     */   private static final String PROTOCOL_HANDLER = "java.protocol.handler.pkgs";
/*     */   private static final String HANDLERS_PACKAGE = "org.springframework.boot.loader";
/*  57 */   private static final AsciiBytes META_INF = new AsciiBytes("META-INF/");
/*     */   
/*  59 */   private static final AsciiBytes SIGNATURE_FILE_EXTENSION = new AsciiBytes(".SF");
/*     */   
/*     */ 
/*     */   private final RandomAccessDataFile rootFile;
/*     */   
/*     */ 
/*     */   private final String pathFromRoot;
/*     */   
/*     */   private final RandomAccessData data;
/*     */   
/*     */   private final JarFileType type;
/*     */   
/*     */   private URL url;
/*     */   
/*     */   private JarFileEntries entries;
/*     */   
/*     */   private SoftReference<Manifest> manifest;
/*     */   
/*     */   private boolean signed;
/*     */   
/*     */ 
/*     */   public JarFile(File file)
/*     */     throws IOException
/*     */   {
/*  83 */     this(new RandomAccessDataFile(file));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   JarFile(RandomAccessDataFile file)
/*     */     throws IOException
/*     */   {
/*  92 */     this(file, "", file, JarFileType.DIRECT);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private JarFile(RandomAccessDataFile rootFile, String pathFromRoot, RandomAccessData data, JarFileType type)
/*     */     throws IOException
/*     */   {
/* 106 */     this(rootFile, pathFromRoot, data, null, type);
/*     */   }
/*     */   
/*     */   private JarFile(RandomAccessDataFile rootFile, String pathFromRoot, RandomAccessData data, JarEntryFilter filter, JarFileType type)
/*     */     throws IOException
/*     */   {
/* 112 */     super(rootFile.getFile());
/* 113 */     this.rootFile = rootFile;
/* 114 */     this.pathFromRoot = pathFromRoot;
/* 115 */     CentralDirectoryParser parser = new CentralDirectoryParser();
/* 116 */     this.entries = ((JarFileEntries)parser.addVisitor(new JarFileEntries(this, filter)));
/* 117 */     parser.addVisitor(centralDirectoryVisitor());
/* 118 */     this.data = parser.parse(data, filter == null);
/* 119 */     this.type = type;
/*     */   }
/*     */   
/*     */   private CentralDirectoryVisitor centralDirectoryVisitor() {
/* 123 */     new CentralDirectoryVisitor()
/*     */     {
/*     */       public void visitStart(CentralDirectoryEndRecord endRecord, RandomAccessData centralDirectoryData) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void visitFileHeader(CentralDirectoryFileHeader fileHeader, int dataOffset)
/*     */       {
/* 133 */         AsciiBytes name = fileHeader.getName();
/* 134 */         if ((name.startsWith(JarFile.META_INF)) && 
/* 135 */           (name.endsWith(JarFile.SIGNATURE_FILE_EXTENSION))) {
/* 136 */           JarFile.this.signed = true;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       public void visitEnd() {}
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */   protected final RandomAccessDataFile getRootJarFile()
/*     */   {
/* 148 */     return this.rootFile;
/*     */   }
/*     */   
/*     */   RandomAccessData getData() {
/* 152 */     return this.data;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public Manifest getManifest()
/*     */     throws IOException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 27	org/springframework/boot/loader/jar/JarFile:manifest	Ljava/lang/ref/SoftReference;
/*     */     //   4: ifnonnull +7 -> 11
/*     */     //   7: aconst_null
/*     */     //   8: goto +13 -> 21
/*     */     //   11: aload_0
/*     */     //   12: getfield 27	org/springframework/boot/loader/jar/JarFile:manifest	Ljava/lang/ref/SoftReference;
/*     */     //   15: invokevirtual 28	java/lang/ref/SoftReference:get	()Ljava/lang/Object;
/*     */     //   18: checkcast 29	java/util/jar/Manifest
/*     */     //   21: astore_1
/*     */     //   22: aload_1
/*     */     //   23: ifnonnull +82 -> 105
/*     */     //   26: aload_0
/*     */     //   27: getfield 24	org/springframework/boot/loader/jar/JarFile:type	Lorg/springframework/boot/loader/jar/JarFile$JarFileType;
/*     */     //   30: getstatic 30	org/springframework/boot/loader/jar/JarFile$JarFileType:NESTED_DIRECTORY	Lorg/springframework/boot/loader/jar/JarFile$JarFileType;
/*     */     //   33: if_acmpne +21 -> 54
/*     */     //   36: new 31	org/springframework/boot/loader/jar/JarFile
/*     */     //   39: dup
/*     */     //   40: aload_0
/*     */     //   41: invokevirtual 32	org/springframework/boot/loader/jar/JarFile:getRootJarFile	()Lorg/springframework/boot/loader/data/RandomAccessDataFile;
/*     */     //   44: invokespecial 6	org/springframework/boot/loader/jar/JarFile:<init>	(Lorg/springframework/boot/loader/data/RandomAccessDataFile;)V
/*     */     //   47: invokevirtual 33	org/springframework/boot/loader/jar/JarFile:getManifest	()Ljava/util/jar/Manifest;
/*     */     //   50: astore_1
/*     */     //   51: goto +42 -> 93
/*     */     //   54: aload_0
/*     */     //   55: ldc 34
/*     */     //   57: getstatic 35	org/springframework/boot/loader/data/RandomAccessData$ResourceAccess:ONCE	Lorg/springframework/boot/loader/data/RandomAccessData$ResourceAccess;
/*     */     //   60: invokevirtual 36	org/springframework/boot/loader/jar/JarFile:getInputStream	(Ljava/lang/String;Lorg/springframework/boot/loader/data/RandomAccessData$ResourceAccess;)Ljava/io/InputStream;
/*     */     //   63: astore_2
/*     */     //   64: aload_2
/*     */     //   65: ifnonnull +5 -> 70
/*     */     //   68: aconst_null
/*     */     //   69: areturn
/*     */     //   70: new 29	java/util/jar/Manifest
/*     */     //   73: dup
/*     */     //   74: aload_2
/*     */     //   75: invokespecial 37	java/util/jar/Manifest:<init>	(Ljava/io/InputStream;)V
/*     */     //   78: astore_1
/*     */     //   79: aload_2
/*     */     //   80: invokevirtual 38	java/io/InputStream:close	()V
/*     */     //   83: goto +10 -> 93
/*     */     //   86: astore_3
/*     */     //   87: aload_2
/*     */     //   88: invokevirtual 38	java/io/InputStream:close	()V
/*     */     //   91: aload_3
/*     */     //   92: athrow
/*     */     //   93: aload_0
/*     */     //   94: new 39	java/lang/ref/SoftReference
/*     */     //   97: dup
/*     */     //   98: aload_1
/*     */     //   99: invokespecial 40	java/lang/ref/SoftReference:<init>	(Ljava/lang/Object;)V
/*     */     //   102: putfield 27	org/springframework/boot/loader/jar/JarFile:manifest	Ljava/lang/ref/SoftReference;
/*     */     //   105: aload_1
/*     */     //   106: areturn
/*     */     // Line number table:
/*     */     //   Java source line #157	-> byte code offset #0
/*     */     //   Java source line #158	-> byte code offset #22
/*     */     //   Java source line #159	-> byte code offset #26
/*     */     //   Java source line #160	-> byte code offset #36
/*     */     //   Java source line #163	-> byte code offset #54
/*     */     //   Java source line #165	-> byte code offset #64
/*     */     //   Java source line #166	-> byte code offset #68
/*     */     //   Java source line #169	-> byte code offset #70
/*     */     //   Java source line #172	-> byte code offset #79
/*     */     //   Java source line #173	-> byte code offset #83
/*     */     //   Java source line #172	-> byte code offset #86
/*     */     //   Java source line #175	-> byte code offset #93
/*     */     //   Java source line #177	-> byte code offset #105
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	107	0	this	JarFile
/*     */     //   21	85	1	manifest	Manifest
/*     */     //   63	25	2	inputStream	InputStream
/*     */     //   86	6	3	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   70	79	86	finally
/*     */   }
/*     */   
/*     */   public Enumeration<java.util.jar.JarEntry> entries()
/*     */   {
/* 182 */     final Iterator<JarEntry> iterator = this.entries.iterator();
/* 183 */     new Enumeration()
/*     */     {
/*     */       public boolean hasMoreElements()
/*     */       {
/* 187 */         return iterator.hasNext();
/*     */       }
/*     */       
/*     */       public java.util.jar.JarEntry nextElement()
/*     */       {
/* 192 */         return (java.util.jar.JarEntry)iterator.next();
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */   public JarEntry getJarEntry(String name)
/*     */   {
/* 200 */     return (JarEntry)getEntry(name);
/*     */   }
/*     */   
/*     */   public boolean containsEntry(String name) {
/* 204 */     return this.entries.containsEntry(name);
/*     */   }
/*     */   
/*     */   public ZipEntry getEntry(String name)
/*     */   {
/* 209 */     return this.entries.getEntry(name);
/*     */   }
/*     */   
/*     */   public synchronized InputStream getInputStream(ZipEntry ze) throws IOException
/*     */   {
/* 214 */     return getInputStream(ze, RandomAccessData.ResourceAccess.PER_READ);
/*     */   }
/*     */   
/*     */   public InputStream getInputStream(ZipEntry ze, RandomAccessData.ResourceAccess access) throws IOException
/*     */   {
/* 219 */     if ((ze instanceof JarEntry)) {
/* 220 */       return this.entries.getInputStream((JarEntry)ze, access);
/*     */     }
/* 222 */     return getInputStream(ze == null ? null : ze.getName(), access);
/*     */   }
/*     */   
/*     */   InputStream getInputStream(String name, RandomAccessData.ResourceAccess access) throws IOException {
/* 226 */     return this.entries.getInputStream(name, access);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized JarFile getNestedJarFile(ZipEntry entry)
/*     */     throws IOException
/*     */   {
/* 237 */     return getNestedJarFile((JarEntry)entry);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized JarFile getNestedJarFile(JarEntry entry)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 248 */       return createJarFileFromEntry(entry);
/*     */     }
/*     */     catch (IOException ex)
/*     */     {
/* 252 */       throw new IOException("Unable to open nested jar file '" + entry.getName() + "'", ex);
/*     */     }
/*     */   }
/*     */   
/*     */   private JarFile createJarFileFromEntry(JarEntry entry) throws IOException {
/* 257 */     if (entry.isDirectory()) {
/* 258 */       return createJarFileFromDirectoryEntry(entry);
/*     */     }
/* 260 */     return createJarFileFromFileEntry(entry);
/*     */   }
/*     */   
/*     */   private JarFile createJarFileFromDirectoryEntry(JarEntry entry) throws IOException {
/* 264 */     final AsciiBytes sourceName = new AsciiBytes(entry.getName());
/* 265 */     JarEntryFilter filter = new JarEntryFilter()
/*     */     {
/*     */       public AsciiBytes apply(AsciiBytes name)
/*     */       {
/* 269 */         if ((name.startsWith(sourceName)) && (!name.equals(sourceName))) {
/* 270 */           return name.substring(sourceName.length());
/*     */         }
/* 272 */         return null;
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 277 */     };
/* 278 */     return new JarFile(this.rootFile, this.pathFromRoot + "!/" + entry.getName().substring(0, sourceName.length() - 1), this.data, filter, JarFileType.NESTED_DIRECTORY);
/*     */   }
/*     */   
/*     */   private JarFile createJarFileFromFileEntry(JarEntry entry) throws IOException
/*     */   {
/* 283 */     if (entry.getMethod() != 0)
/*     */     {
/* 285 */       throw new IllegalStateException("Unable to open nested entry '" + entry.getName() + "'. It has been compressed and nested " + "jar files must be stored without compression. Please check the " + "mechanism used to create your executable jar file");
/*     */     }
/*     */     
/*     */ 
/* 289 */     RandomAccessData entryData = this.entries.getEntryData(entry.getName());
/* 290 */     return new JarFile(this.rootFile, this.pathFromRoot + "!/" + entry.getName(), entryData, JarFileType.NESTED_JAR);
/*     */   }
/*     */   
/*     */ 
/*     */   public int size()
/*     */   {
/* 296 */     return (int)this.data.getSize();
/*     */   }
/*     */   
/*     */   public void close() throws IOException
/*     */   {
/* 301 */     this.rootFile.close();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public URL getUrl()
/*     */     throws MalformedURLException
/*     */   {
/* 311 */     if (this.url == null) {
/* 312 */       Handler handler = new Handler(this);
/* 313 */       String file = this.rootFile.getFile().toURI() + this.pathFromRoot + "!/";
/* 314 */       file = file.replace("file:////", "file://");
/* 315 */       this.url = new URL("jar", "", -1, file, handler);
/*     */     }
/* 317 */     return this.url;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 322 */     return getName();
/*     */   }
/*     */   
/*     */   public String getName()
/*     */   {
/* 327 */     return this.rootFile.getFile() + this.pathFromRoot;
/*     */   }
/*     */   
/*     */   boolean isSigned() {
/* 331 */     return this.signed;
/*     */   }
/*     */   
/*     */ 
/*     */   void setupEntryCertificates(JarEntry entry)
/*     */   {
/*     */     try
/*     */     {
/* 339 */       JarInputStream inputStream = new JarInputStream(getData().getInputStream(RandomAccessData.ResourceAccess.ONCE));
/*     */       try {
/* 341 */         java.util.jar.JarEntry certEntry = inputStream.getNextJarEntry();
/* 342 */         while (certEntry != null) {
/* 343 */           inputStream.closeEntry();
/* 344 */           if (entry.getName().equals(certEntry.getName())) {
/* 345 */             setCertificates(entry, certEntry);
/*     */           }
/* 347 */           setCertificates(getJarEntry(certEntry.getName()), certEntry);
/* 348 */           certEntry = inputStream.getNextJarEntry();
/*     */         }
/*     */       }
/*     */       finally {
/* 352 */         inputStream.close();
/*     */       }
/*     */     }
/*     */     catch (IOException ex) {
/* 356 */       throw new IllegalStateException(ex);
/*     */     }
/*     */   }
/*     */   
/*     */   private void setCertificates(JarEntry entry, java.util.jar.JarEntry certEntry) {
/* 361 */     if (entry != null) {
/* 362 */       entry.setCertificates(certEntry);
/*     */     }
/*     */   }
/*     */   
/*     */   public void clearCache() {
/* 367 */     this.entries.clearCache();
/*     */   }
/*     */   
/*     */   protected String getPathFromRoot() {
/* 371 */     return this.pathFromRoot;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void registerUrlProtocolHandler()
/*     */   {
/* 379 */     String handlers = System.getProperty("java.protocol.handler.pkgs");
/* 380 */     System.setProperty("java.protocol.handler.pkgs", handlers + "|" + "org.springframework.boot.loader");
/*     */     
/* 382 */     resetCachedUrlHandlers();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void resetCachedUrlHandlers()
/*     */   {
/*     */     try
/*     */     {
/* 392 */       URL.setURLStreamHandlerFactory(null);
/*     */     }
/*     */     catch (Error localError) {}
/*     */   }
/*     */   
/*     */ 
/*     */   private static enum JarFileType
/*     */   {
/* 400 */     DIRECT,  NESTED_DIRECTORY,  NESTED_JAR;
/*     */     
/*     */     private JarFileType() {}
/*     */   }
/*     */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\jar\JarFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */