/*     */ package org.springframework.boot.loader.archive;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Deque;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
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
/*     */ public class ExplodedArchive
/*     */   implements Archive
/*     */ {
/*  45 */   private static final Set<String> SKIPPED_NAMES = new HashSet(
/*  46 */     Arrays.asList(new String[] { ".", ".." }));
/*     */   
/*     */ 
/*     */   private final File root;
/*     */   
/*     */ 
/*     */   private final boolean recursive;
/*     */   
/*     */   private File manifestFile;
/*     */   
/*     */   private Manifest manifest;
/*     */   
/*     */ 
/*     */   public ExplodedArchive(File root)
/*     */   {
/*  61 */     this(root, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ExplodedArchive(File root, boolean recursive)
/*     */   {
/*  72 */     if ((!root.exists()) || (!root.isDirectory())) {
/*  73 */       throw new IllegalArgumentException("Invalid source folder " + root);
/*     */     }
/*  75 */     this.root = root;
/*  76 */     this.recursive = recursive;
/*  77 */     this.manifestFile = getManifestFile(root);
/*     */   }
/*     */   
/*     */   private File getManifestFile(File root) {
/*  81 */     File metaInf = new File(root, "META-INF");
/*  82 */     return new File(metaInf, "MANIFEST.MF");
/*     */   }
/*     */   
/*     */   public URL getUrl() throws MalformedURLException
/*     */   {
/*  87 */     return this.root.toURI().toURL();
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public Manifest getManifest()
/*     */     throws IOException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 24	org/springframework/boot/loader/archive/ExplodedArchive:manifest	Ljava/util/jar/Manifest;
/*     */     //   4: ifnonnull +51 -> 55
/*     */     //   7: aload_0
/*     */     //   8: getfield 17	org/springframework/boot/loader/archive/ExplodedArchive:manifestFile	Ljava/io/File;
/*     */     //   11: invokevirtual 4	java/io/File:exists	()Z
/*     */     //   14: ifeq +41 -> 55
/*     */     //   17: new 25	java/io/FileInputStream
/*     */     //   20: dup
/*     */     //   21: aload_0
/*     */     //   22: getfield 17	org/springframework/boot/loader/archive/ExplodedArchive:manifestFile	Ljava/io/File;
/*     */     //   25: invokespecial 26	java/io/FileInputStream:<init>	(Ljava/io/File;)V
/*     */     //   28: astore_1
/*     */     //   29: aload_0
/*     */     //   30: new 27	java/util/jar/Manifest
/*     */     //   33: dup
/*     */     //   34: aload_1
/*     */     //   35: invokespecial 28	java/util/jar/Manifest:<init>	(Ljava/io/InputStream;)V
/*     */     //   38: putfield 24	org/springframework/boot/loader/archive/ExplodedArchive:manifest	Ljava/util/jar/Manifest;
/*     */     //   41: aload_1
/*     */     //   42: invokevirtual 29	java/io/FileInputStream:close	()V
/*     */     //   45: goto +10 -> 55
/*     */     //   48: astore_2
/*     */     //   49: aload_1
/*     */     //   50: invokevirtual 29	java/io/FileInputStream:close	()V
/*     */     //   53: aload_2
/*     */     //   54: athrow
/*     */     //   55: aload_0
/*     */     //   56: getfield 24	org/springframework/boot/loader/archive/ExplodedArchive:manifest	Ljava/util/jar/Manifest;
/*     */     //   59: areturn
/*     */     // Line number table:
/*     */     //   Java source line #92	-> byte code offset #0
/*     */     //   Java source line #93	-> byte code offset #17
/*     */     //   Java source line #95	-> byte code offset #29
/*     */     //   Java source line #98	-> byte code offset #41
/*     */     //   Java source line #99	-> byte code offset #45
/*     */     //   Java source line #98	-> byte code offset #48
/*     */     //   Java source line #101	-> byte code offset #55
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	60	0	this	ExplodedArchive
/*     */     //   28	22	1	inputStream	java.io.FileInputStream
/*     */     //   48	6	2	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   29	41	48	finally
/*     */   }
/*     */   
/*     */   public List<Archive> getNestedArchives(Archive.EntryFilter filter)
/*     */     throws IOException
/*     */   {
/* 106 */     List<Archive> nestedArchives = new ArrayList();
/* 107 */     for (Archive.Entry entry : this) {
/* 108 */       if (filter.matches(entry)) {
/* 109 */         nestedArchives.add(getNestedArchive(entry));
/*     */       }
/*     */     }
/* 112 */     return Collections.unmodifiableList(nestedArchives);
/*     */   }
/*     */   
/*     */   public Iterator<Archive.Entry> iterator()
/*     */   {
/* 117 */     return new FileEntryIterator(this.root, this.recursive);
/*     */   }
/*     */   
/*     */   protected Archive getNestedArchive(Archive.Entry entry) throws IOException {
/* 121 */     File file = ((FileEntry)entry).getFile();
/* 122 */     return file.isDirectory() ? new ExplodedArchive(file) : new JarFileArchive(file);
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/*     */     try
/*     */     {
/* 129 */       return getUrl().toString();
/*     */     }
/*     */     catch (Exception ex) {}
/* 132 */     return "exploded archive";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class FileEntryIterator
/*     */     implements Iterator<Archive.Entry>
/*     */   {
/* 141 */     private final Comparator<File> entryComparator = new EntryComparator(null);
/*     */     
/*     */     private final File root;
/*     */     
/*     */     private final boolean recursive;
/*     */     
/* 147 */     private final Deque<Iterator<File>> stack = new LinkedList();
/*     */     private File current;
/*     */     
/*     */     FileEntryIterator(File root, boolean recursive)
/*     */     {
/* 152 */       this.root = root;
/* 153 */       this.recursive = recursive;
/* 154 */       this.stack.add(listFiles(root));
/* 155 */       this.current = poll();
/*     */     }
/*     */     
/*     */     public boolean hasNext()
/*     */     {
/* 160 */       return this.current != null;
/*     */     }
/*     */     
/*     */     public Archive.Entry next()
/*     */     {
/* 165 */       if (this.current == null) {
/* 166 */         throw new NoSuchElementException();
/*     */       }
/* 168 */       File file = this.current;
/* 169 */       if ((file.isDirectory()) && ((this.recursive) || 
/* 170 */         (file.getParentFile().equals(this.root)))) {
/* 171 */         this.stack.addFirst(listFiles(file));
/*     */       }
/* 173 */       this.current = poll();
/*     */       
/* 175 */       String name = file.toURI().getPath().substring(this.root.toURI().getPath().length());
/* 176 */       return new ExplodedArchive.FileEntry(name, file);
/*     */     }
/*     */     
/*     */     private Iterator<File> listFiles(File file) {
/* 180 */       File[] files = file.listFiles();
/* 181 */       if (files == null) {
/* 182 */         return Collections.emptyList().iterator();
/*     */       }
/* 184 */       Arrays.sort(files, this.entryComparator);
/* 185 */       return Arrays.asList(files).iterator();
/*     */     }
/*     */     
/*     */     private File poll() {
/* 189 */       while (!this.stack.isEmpty()) {
/* 190 */         while (((Iterator)this.stack.peek()).hasNext()) {
/* 191 */           File file = (File)((Iterator)this.stack.peek()).next();
/* 192 */           if (!ExplodedArchive.SKIPPED_NAMES.contains(file.getName())) {
/* 193 */             return file;
/*     */           }
/*     */         }
/* 196 */         this.stack.poll();
/*     */       }
/* 198 */       return null;
/*     */     }
/*     */     
/*     */     public void remove()
/*     */     {
/* 203 */       throw new UnsupportedOperationException("remove");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private static class EntryComparator
/*     */       implements Comparator<File>
/*     */     {
/*     */       public int compare(File o1, File o2)
/*     */       {
/* 213 */         return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class FileEntry
/*     */     implements Archive.Entry
/*     */   {
/*     */     private final String name;
/*     */     
/*     */     private final File file;
/*     */     
/*     */ 
/*     */     FileEntry(String name, File file)
/*     */     {
/* 230 */       this.name = name;
/* 231 */       this.file = file;
/*     */     }
/*     */     
/*     */     public File getFile() {
/* 235 */       return this.file;
/*     */     }
/*     */     
/*     */     public boolean isDirectory()
/*     */     {
/* 240 */       return this.file.isDirectory();
/*     */     }
/*     */     
/*     */     public String getName()
/*     */     {
/* 245 */       return this.name;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\archive\ExplodedArchive.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */