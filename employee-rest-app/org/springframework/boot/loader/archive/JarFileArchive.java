/*     */ package org.springframework.boot.loader.archive;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.Manifest;
/*     */ import org.springframework.boot.loader.data.RandomAccessData.ResourceAccess;
/*     */ import org.springframework.boot.loader.jar.JarFile;
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
/*     */ public class JarFileArchive
/*     */   implements Archive
/*     */ {
/*     */   private static final String UNPACK_MARKER = "UNPACK:";
/*     */   private static final int BUFFER_SIZE = 32768;
/*     */   private final JarFile jarFile;
/*     */   private URL url;
/*     */   private File tempUnpackFolder;
/*     */   
/*     */   public JarFileArchive(File file)
/*     */     throws IOException
/*     */   {
/*  57 */     this(file, null);
/*     */   }
/*     */   
/*     */   public JarFileArchive(File file, URL url) throws IOException {
/*  61 */     this(new JarFile(file));
/*  62 */     this.url = url;
/*     */   }
/*     */   
/*     */   public JarFileArchive(JarFile jarFile) {
/*  66 */     this.jarFile = jarFile;
/*     */   }
/*     */   
/*     */   public URL getUrl() throws MalformedURLException
/*     */   {
/*  71 */     if (this.url != null) {
/*  72 */       return this.url;
/*     */     }
/*  74 */     return this.jarFile.getUrl();
/*     */   }
/*     */   
/*     */   public Manifest getManifest() throws IOException
/*     */   {
/*  79 */     return this.jarFile.getManifest();
/*     */   }
/*     */   
/*     */   public List<Archive> getNestedArchives(Archive.EntryFilter filter) throws IOException
/*     */   {
/*  84 */     List<Archive> nestedArchives = new ArrayList();
/*  85 */     for (Archive.Entry entry : this) {
/*  86 */       if (filter.matches(entry)) {
/*  87 */         nestedArchives.add(getNestedArchive(entry));
/*     */       }
/*     */     }
/*  90 */     return Collections.unmodifiableList(nestedArchives);
/*     */   }
/*     */   
/*     */   public Iterator<Archive.Entry> iterator()
/*     */   {
/*  95 */     return new EntryIterator(this.jarFile.entries());
/*     */   }
/*     */   
/*     */   protected Archive getNestedArchive(Archive.Entry entry) throws IOException {
/*  99 */     JarEntry jarEntry = ((JarFileEntry)entry).getJarEntry();
/* 100 */     if (jarEntry.getComment().startsWith("UNPACK:")) {
/* 101 */       return getUnpackedNestedArchive(jarEntry);
/*     */     }
/* 103 */     JarFile jarFile = this.jarFile.getNestedJarFile(jarEntry);
/* 104 */     return new JarFileArchive(jarFile);
/*     */   }
/*     */   
/*     */   private Archive getUnpackedNestedArchive(JarEntry jarEntry) throws IOException {
/* 108 */     String name = jarEntry.getName();
/* 109 */     if (name.lastIndexOf("/") != -1) {
/* 110 */       name = name.substring(name.lastIndexOf("/") + 1);
/*     */     }
/* 112 */     File file = new File(getTempUnpackFolder(), name);
/* 113 */     if ((!file.exists()) || (file.length() != jarEntry.getSize())) {
/* 114 */       unpack(jarEntry, file);
/*     */     }
/* 116 */     return new JarFileArchive(file, file.toURI().toURL());
/*     */   }
/*     */   
/*     */   private File getTempUnpackFolder() {
/* 120 */     if (this.tempUnpackFolder == null) {
/* 121 */       File tempFolder = new File(System.getProperty("java.io.tmpdir"));
/* 122 */       this.tempUnpackFolder = createUnpackFolder(tempFolder);
/*     */     }
/* 124 */     return this.tempUnpackFolder;
/*     */   }
/*     */   
/*     */   private File createUnpackFolder(File parent) {
/* 128 */     int attempts = 0;
/* 129 */     while (attempts++ < 1000) {
/* 130 */       String fileName = new File(this.jarFile.getName()).getName();
/*     */       
/* 132 */       File unpackFolder = new File(parent, fileName + "-spring-boot-libs-" + UUID.randomUUID());
/* 133 */       if (unpackFolder.mkdirs()) {
/* 134 */         return unpackFolder;
/*     */       }
/*     */     }
/* 137 */     throw new IllegalStateException("Failed to create unpack folder in directory '" + parent + "'");
/*     */   }
/*     */   
/*     */   private void unpack(JarEntry entry, File file) throws IOException
/*     */   {
/* 142 */     InputStream inputStream = this.jarFile.getInputStream(entry, RandomAccessData.ResourceAccess.ONCE);
/*     */     try {
/* 144 */       OutputStream outputStream = new FileOutputStream(file);
/*     */       try {
/* 146 */         byte[] buffer = new byte[32768];
/* 147 */         int bytesRead = -1;
/* 148 */         while ((bytesRead = inputStream.read(buffer)) != -1) {
/* 149 */           outputStream.write(buffer, 0, bytesRead);
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       finally {}
/*     */     }
/*     */     finally
/*     */     {
/* 158 */       inputStream.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/*     */     try {
/* 165 */       return getUrl().toString();
/*     */     }
/*     */     catch (Exception ex) {}
/* 168 */     return "jar archive";
/*     */   }
/*     */   
/*     */ 
/*     */   private static class EntryIterator
/*     */     implements Iterator<Archive.Entry>
/*     */   {
/*     */     private final Enumeration<JarEntry> enumeration;
/*     */     
/*     */ 
/*     */     EntryIterator(Enumeration<JarEntry> enumeration)
/*     */     {
/* 180 */       this.enumeration = enumeration;
/*     */     }
/*     */     
/*     */     public boolean hasNext()
/*     */     {
/* 185 */       return this.enumeration.hasMoreElements();
/*     */     }
/*     */     
/*     */     public Archive.Entry next()
/*     */     {
/* 190 */       return new JarFileArchive.JarFileEntry((JarEntry)this.enumeration.nextElement());
/*     */     }
/*     */     
/*     */     public void remove()
/*     */     {
/* 195 */       throw new UnsupportedOperationException("remove");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static class JarFileEntry
/*     */     implements Archive.Entry
/*     */   {
/*     */     private final JarEntry jarEntry;
/*     */     
/*     */ 
/*     */     JarFileEntry(JarEntry jarEntry)
/*     */     {
/* 208 */       this.jarEntry = jarEntry;
/*     */     }
/*     */     
/*     */     public JarEntry getJarEntry() {
/* 212 */       return this.jarEntry;
/*     */     }
/*     */     
/*     */     public boolean isDirectory()
/*     */     {
/* 217 */       return this.jarEntry.isDirectory();
/*     */     }
/*     */     
/*     */     public String getName()
/*     */     {
/* 222 */       return this.jarEntry.getName().toString();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\archive\JarFileArchive.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */