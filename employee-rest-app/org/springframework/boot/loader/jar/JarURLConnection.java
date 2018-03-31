/*     */ package org.springframework.boot.loader.jar;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FilePermission;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.net.URLEncoder;
/*     */ import java.net.URLStreamHandler;
/*     */ import java.security.Permission;
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
/*     */ final class JarURLConnection
/*     */   extends java.net.JarURLConnection
/*     */ {
/*  40 */   private static ThreadLocal<Boolean> useFastExceptions = new ThreadLocal();
/*     */   
/*  42 */   private static final FileNotFoundException FILE_NOT_FOUND_EXCEPTION = new FileNotFoundException("Jar file or entry not found");
/*     */   
/*     */ 
/*  45 */   private static final IllegalStateException NOT_FOUND_CONNECTION_EXCEPTION = new IllegalStateException(FILE_NOT_FOUND_EXCEPTION);
/*     */   
/*     */   private static final String SEPARATOR = "!/";
/*     */   private static final URL EMPTY_JAR_URL;
/*     */   
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  54 */       EMPTY_JAR_URL = new URL("jar:", null, 0, "file:!/", new URLStreamHandler()
/*     */       {
/*     */         protected URLConnection openConnection(URL u)
/*     */           throws IOException
/*     */         {
/*  59 */           return null;
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (MalformedURLException ex) {
/*  64 */       throw new IllegalStateException(ex);
/*     */     }
/*     */   }
/*     */   
/*  68 */   private static final JarEntryName EMPTY_JAR_ENTRY_NAME = new JarEntryName("");
/*     */   
/*     */ 
/*     */   private static final String READ_ACTION = "read";
/*     */   
/*  73 */   private static final JarURLConnection NOT_FOUND_CONNECTION = notFound();
/*     */   
/*     */   private final JarFile jarFile;
/*     */   
/*     */   private Permission permission;
/*     */   
/*     */   private URL jarFileUrl;
/*     */   
/*     */   private final JarEntryName jarEntryName;
/*     */   
/*     */   private JarEntry jarEntry;
/*     */   
/*     */   private JarURLConnection(URL url, JarFile jarFile, JarEntryName jarEntryName)
/*     */     throws IOException
/*     */   {
/*  88 */     super(EMPTY_JAR_URL);
/*  89 */     this.url = url;
/*  90 */     this.jarFile = jarFile;
/*  91 */     this.jarEntryName = jarEntryName;
/*     */   }
/*     */   
/*     */   public void connect() throws IOException
/*     */   {
/*  96 */     if (this.jarFile == null) {
/*  97 */       throw FILE_NOT_FOUND_EXCEPTION;
/*     */     }
/*  99 */     if ((!this.jarEntryName.isEmpty()) && (this.jarEntry == null)) {
/* 100 */       this.jarEntry = this.jarFile.getJarEntry(getEntryName());
/* 101 */       if (this.jarEntry == null) {
/* 102 */         throwFileNotFound(this.jarEntryName, this.jarFile);
/*     */       }
/*     */     }
/* 105 */     this.connected = true;
/*     */   }
/*     */   
/*     */   public JarFile getJarFile() throws IOException
/*     */   {
/* 110 */     connect();
/* 111 */     return this.jarFile;
/*     */   }
/*     */   
/*     */   public URL getJarFileURL()
/*     */   {
/* 116 */     if (this.jarFile == null) {
/* 117 */       throw NOT_FOUND_CONNECTION_EXCEPTION;
/*     */     }
/* 119 */     if (this.jarFileUrl == null) {
/* 120 */       this.jarFileUrl = buildJarFileUrl();
/*     */     }
/* 122 */     return this.jarFileUrl;
/*     */   }
/*     */   
/*     */   private URL buildJarFileUrl() {
/*     */     try {
/* 127 */       String spec = this.jarFile.getUrl().getFile();
/* 128 */       if (spec.endsWith("!/")) {
/* 129 */         spec = spec.substring(0, spec.length() - "!/".length());
/*     */       }
/* 131 */       if (spec.indexOf("!/") == -1) {
/* 132 */         return new URL(spec);
/*     */       }
/* 134 */       return new URL("jar:" + spec);
/*     */     }
/*     */     catch (MalformedURLException ex) {
/* 137 */       throw new IllegalStateException(ex);
/*     */     }
/*     */   }
/*     */   
/*     */   public JarEntry getJarEntry() throws IOException
/*     */   {
/* 143 */     if ((this.jarEntryName == null) || (this.jarEntryName.isEmpty())) {
/* 144 */       return null;
/*     */     }
/* 146 */     connect();
/* 147 */     return this.jarEntry;
/*     */   }
/*     */   
/*     */   public String getEntryName()
/*     */   {
/* 152 */     if (this.jarFile == null) {
/* 153 */       throw NOT_FOUND_CONNECTION_EXCEPTION;
/*     */     }
/* 155 */     return this.jarEntryName.toString();
/*     */   }
/*     */   
/*     */   public InputStream getInputStream() throws IOException
/*     */   {
/* 160 */     if (this.jarFile == null) {
/* 161 */       throw FILE_NOT_FOUND_EXCEPTION;
/*     */     }
/* 163 */     if (this.jarEntryName.isEmpty()) {
/* 164 */       throw new IOException("no entry name specified");
/*     */     }
/* 166 */     connect();
/* 167 */     InputStream inputStream = this.jarFile.getInputStream(this.jarEntry);
/* 168 */     if (inputStream == null) {
/* 169 */       throwFileNotFound(this.jarEntryName, this.jarFile);
/*     */     }
/* 171 */     return inputStream;
/*     */   }
/*     */   
/*     */   private void throwFileNotFound(Object entry, JarFile jarFile) throws FileNotFoundException
/*     */   {
/* 176 */     if (Boolean.TRUE.equals(useFastExceptions.get())) {
/* 177 */       throw FILE_NOT_FOUND_EXCEPTION;
/*     */     }
/*     */     
/* 180 */     throw new FileNotFoundException("JAR entry " + entry + " not found in " + jarFile.getName());
/*     */   }
/*     */   
/*     */   public int getContentLength()
/*     */   {
/* 185 */     if (this.jarFile == null) {
/* 186 */       return -1;
/*     */     }
/*     */     try {
/* 189 */       if (this.jarEntryName.isEmpty()) {
/* 190 */         return this.jarFile.size();
/*     */       }
/* 192 */       JarEntry entry = getJarEntry();
/* 193 */       return entry == null ? -1 : (int)entry.getSize();
/*     */     }
/*     */     catch (IOException ex) {}
/* 196 */     return -1;
/*     */   }
/*     */   
/*     */   public Object getContent()
/*     */     throws IOException
/*     */   {
/* 202 */     connect();
/* 203 */     return this.jarEntryName.isEmpty() ? this.jarFile : super.getContent();
/*     */   }
/*     */   
/*     */   public String getContentType()
/*     */   {
/* 208 */     return this.jarEntryName == null ? null : this.jarEntryName.getContentType();
/*     */   }
/*     */   
/*     */   public Permission getPermission() throws IOException
/*     */   {
/* 213 */     if (this.jarFile == null) {
/* 214 */       throw FILE_NOT_FOUND_EXCEPTION;
/*     */     }
/* 216 */     if (this.permission == null)
/*     */     {
/* 218 */       this.permission = new FilePermission(this.jarFile.getRootJarFile().getFile().getPath(), "read");
/*     */     }
/* 220 */     return this.permission;
/*     */   }
/*     */   
/*     */   static void setUseFastExceptions(boolean useFastExceptions) {
/* 224 */     useFastExceptions.set(Boolean.valueOf(useFastExceptions));
/*     */   }
/*     */   
/*     */   static JarURLConnection get(URL url, JarFile jarFile) throws IOException {
/* 228 */     String spec = extractFullSpec(url, jarFile.getPathFromRoot());
/*     */     
/* 230 */     int index = 0;
/* 231 */     int separator; while ((separator = spec.indexOf("!/", index)) > 0) {
/* 232 */       String entryName = spec.substring(index, separator);
/* 233 */       JarEntry jarEntry = jarFile.getJarEntry(entryName);
/* 234 */       if (jarEntry == null) {
/* 235 */         return notFound(jarFile, JarEntryName.get(entryName));
/*     */       }
/* 237 */       jarFile = jarFile.getNestedJarFile(jarEntry);
/* 238 */       index += separator + "!/".length();
/*     */     }
/* 240 */     JarEntryName jarEntryName = JarEntryName.get(spec, index);
/* 241 */     if ((Boolean.TRUE.equals(useFastExceptions.get())) && 
/* 242 */       (!jarEntryName.isEmpty()) && 
/* 243 */       (!jarFile.containsEntry(jarEntryName.toString()))) {
/* 244 */       return NOT_FOUND_CONNECTION;
/*     */     }
/*     */     
/* 247 */     return new JarURLConnection(url, jarFile, jarEntryName);
/*     */   }
/*     */   
/*     */   private static String extractFullSpec(URL url, String pathFromRoot) {
/* 251 */     String file = url.getFile();
/* 252 */     int separatorIndex = file.indexOf("!/");
/* 253 */     if (separatorIndex < 0) {
/* 254 */       return "";
/*     */     }
/* 256 */     int specIndex = separatorIndex + "!/".length() + pathFromRoot.length();
/* 257 */     return file.substring(specIndex);
/*     */   }
/*     */   
/*     */   private static JarURLConnection notFound() {
/*     */     try {
/* 262 */       return notFound(null, null);
/*     */     }
/*     */     catch (IOException ex) {
/* 265 */       throw new IllegalStateException(ex);
/*     */     }
/*     */   }
/*     */   
/*     */   private static JarURLConnection notFound(JarFile jarFile, JarEntryName jarEntryName) throws IOException
/*     */   {
/* 271 */     if (Boolean.TRUE.equals(useFastExceptions.get())) {
/* 272 */       return NOT_FOUND_CONNECTION;
/*     */     }
/* 274 */     return new JarURLConnection(null, jarFile, jarEntryName);
/*     */   }
/*     */   
/*     */ 
/*     */   static class JarEntryName
/*     */   {
/*     */     private final String name;
/*     */     
/*     */     private String contentType;
/*     */     
/*     */ 
/*     */     JarEntryName(String spec)
/*     */     {
/* 287 */       this.name = decode(spec);
/*     */     }
/*     */     
/*     */     private String decode(String source) {
/* 291 */       if ((source.length() == 0) || (source.indexOf('%') < 0)) {
/* 292 */         return source;
/*     */       }
/* 294 */       ByteArrayOutputStream bos = new ByteArrayOutputStream(source.length());
/* 295 */       write(source, bos);
/*     */       
/* 297 */       return AsciiBytes.toString(bos.toByteArray());
/*     */     }
/*     */     
/*     */     private void write(String source, ByteArrayOutputStream outputStream) {
/* 301 */       int length = source.length();
/* 302 */       for (int i = 0; i < length; i++) {
/* 303 */         int c = source.charAt(i);
/* 304 */         if (c > 127) {
/*     */           try {
/* 306 */             String encoded = URLEncoder.encode(String.valueOf((char)c), "UTF-8");
/*     */             
/* 308 */             write(encoded, outputStream);
/*     */           }
/*     */           catch (UnsupportedEncodingException ex) {
/* 311 */             throw new IllegalStateException(ex);
/*     */           }
/*     */         }
/*     */         else {
/* 315 */           if (c == 37) {
/* 316 */             if (i + 2 >= length)
/*     */             {
/* 318 */               throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
/*     */             }
/*     */             
/* 321 */             c = decodeEscapeSequence(source, i);
/* 322 */             i += 2;
/*     */           }
/* 324 */           outputStream.write(c);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     private char decodeEscapeSequence(String source, int i) {
/* 330 */       int hi = Character.digit(source.charAt(i + 1), 16);
/* 331 */       int lo = Character.digit(source.charAt(i + 2), 16);
/* 332 */       if ((hi == -1) || (lo == -1))
/*     */       {
/* 334 */         throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
/*     */       }
/* 336 */       return (char)((hi << 4) + lo);
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 341 */       return this.name;
/*     */     }
/*     */     
/*     */     public boolean isEmpty() {
/* 345 */       return this.name.length() == 0;
/*     */     }
/*     */     
/*     */     public String getContentType() {
/* 349 */       if (this.contentType == null) {
/* 350 */         this.contentType = deduceContentType();
/*     */       }
/* 352 */       return this.contentType;
/*     */     }
/*     */     
/*     */     private String deduceContentType()
/*     */     {
/* 357 */       String type = isEmpty() ? "x-java/jar" : null;
/* 358 */       type = type != null ? type : URLConnection.guessContentTypeFromName(toString());
/* 359 */       type = type != null ? type : "content/unknown";
/* 360 */       return type;
/*     */     }
/*     */     
/*     */     public static JarEntryName get(String spec) {
/* 364 */       return get(spec, 0);
/*     */     }
/*     */     
/*     */     public static JarEntryName get(String spec, int beginIndex) {
/* 368 */       if (spec.length() <= beginIndex) {
/* 369 */         return JarURLConnection.EMPTY_JAR_ENTRY_NAME;
/*     */       }
/* 371 */       return new JarEntryName(spec.substring(beginIndex));
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\jar\JarURLConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */