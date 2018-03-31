/*     */ package org.springframework.boot.loader.jar;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.net.URLDecoder;
/*     */ import java.net.URLStreamHandler;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
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
/*     */ public class Handler
/*     */   extends URLStreamHandler
/*     */ {
/*     */   private static final String FILE_PROTOCOL = "file:";
/*     */   private static final String SEPARATOR = "!/";
/*  48 */   private static final String[] FALLBACK_HANDLERS = { "sun.net.www.protocol.jar.Handler" };
/*     */   
/*     */   private static final Method OPEN_CONNECTION_METHOD;
/*     */   
/*     */   static
/*     */   {
/*  54 */     Method method = null;
/*     */     try {
/*  56 */       method = URLStreamHandler.class.getDeclaredMethod("openConnection", new Class[] { URL.class });
/*     */     }
/*     */     catch (Exception localException) {}
/*     */     
/*     */ 
/*     */ 
/*  62 */     OPEN_CONNECTION_METHOD = method;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  68 */   private static SoftReference<Map<File, JarFile>> rootFileCache = new SoftReference(null);
/*     */   
/*     */ 
/*  71 */   private final Logger logger = Logger.getLogger(getClass().getName());
/*     */   
/*     */   private final JarFile jarFile;
/*     */   private URLStreamHandler fallbackHandler;
/*     */   
/*     */   public Handler()
/*     */   {
/*  78 */     this(null);
/*     */   }
/*     */   
/*     */   public Handler(JarFile jarFile) {
/*  82 */     this.jarFile = jarFile;
/*     */   }
/*     */   
/*     */   protected URLConnection openConnection(URL url) throws IOException
/*     */   {
/*  87 */     if (this.jarFile != null) {
/*  88 */       return JarURLConnection.get(url, this.jarFile);
/*     */     }
/*     */     try {
/*  91 */       return JarURLConnection.get(url, getRootJarFileFromUrl(url));
/*     */     }
/*     */     catch (Exception ex) {
/*  94 */       return openFallbackConnection(url, ex);
/*     */     }
/*     */   }
/*     */   
/*     */   private URLConnection openFallbackConnection(URL url, Exception reason) throws IOException
/*     */   {
/*     */     try {
/* 101 */       return openConnection(getFallbackHandler(), url);
/*     */     }
/*     */     catch (Exception ex) {
/* 104 */       if ((reason instanceof IOException)) {
/* 105 */         this.logger.log(Level.FINEST, "Unable to open fallback handler", ex);
/* 106 */         throw ((IOException)reason);
/*     */       }
/* 108 */       this.logger.log(Level.WARNING, "Unable to open fallback handler", ex);
/* 109 */       if ((reason instanceof RuntimeException)) {
/* 110 */         throw ((RuntimeException)reason);
/*     */       }
/* 112 */       throw new IllegalStateException(reason);
/*     */     }
/*     */   }
/*     */   
/*     */   private URLStreamHandler getFallbackHandler() {
/* 117 */     if (this.fallbackHandler != null) {
/* 118 */       return this.fallbackHandler;
/*     */     }
/* 120 */     for (String handlerClassName : FALLBACK_HANDLERS) {
/*     */       try {
/* 122 */         Class<?> handlerClass = Class.forName(handlerClassName);
/* 123 */         this.fallbackHandler = ((URLStreamHandler)handlerClass.newInstance());
/* 124 */         return this.fallbackHandler;
/*     */       }
/*     */       catch (Exception localException) {}
/*     */     }
/*     */     
/*     */ 
/* 130 */     throw new IllegalStateException("Unable to find fallback handler");
/*     */   }
/*     */   
/*     */   private URLConnection openConnection(URLStreamHandler handler, URL url) throws Exception
/*     */   {
/* 135 */     if (OPEN_CONNECTION_METHOD == null) {
/* 136 */       throw new IllegalStateException("Unable to invoke fallback open connection method");
/*     */     }
/*     */     
/* 139 */     OPEN_CONNECTION_METHOD.setAccessible(true);
/* 140 */     return (URLConnection)OPEN_CONNECTION_METHOD.invoke(handler, new Object[] { url });
/*     */   }
/*     */   
/*     */   public JarFile getRootJarFileFromUrl(URL url) throws IOException {
/* 144 */     String spec = url.getFile();
/* 145 */     int separatorIndex = spec.indexOf("!/");
/* 146 */     if (separatorIndex == -1) {
/* 147 */       throw new MalformedURLException("Jar URL does not contain !/ separator");
/*     */     }
/* 149 */     String name = spec.substring(0, separatorIndex);
/* 150 */     return getRootJarFile(name);
/*     */   }
/*     */   
/*     */   private JarFile getRootJarFile(String name) throws IOException {
/*     */     try {
/* 155 */       if (!name.startsWith("file:")) {
/* 156 */         throw new IllegalStateException("Not a file URL");
/*     */       }
/* 158 */       String path = name.substring("file:".length());
/* 159 */       File file = new File(URLDecoder.decode(path, "UTF-8"));
/* 160 */       Map<File, JarFile> cache = (Map)rootFileCache.get();
/* 161 */       JarFile jarFile = cache == null ? null : (JarFile)cache.get(file);
/* 162 */       if (jarFile == null) {
/* 163 */         jarFile = new JarFile(file);
/* 164 */         addToRootFileCache(file, jarFile);
/*     */       }
/* 166 */       return jarFile;
/*     */     }
/*     */     catch (Exception ex) {
/* 169 */       throw new IOException("Unable to open root Jar file '" + name + "'", ex);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static void addToRootFileCache(File sourceFile, JarFile jarFile)
/*     */   {
/* 179 */     Map<File, JarFile> cache = (Map)rootFileCache.get();
/* 180 */     if (cache == null) {
/* 181 */       cache = new ConcurrentHashMap();
/* 182 */       rootFileCache = new SoftReference(cache);
/*     */     }
/* 184 */     cache.put(sourceFile, jarFile);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setUseFastConnectionExceptions(boolean useFastConnectionExceptions)
/*     */   {
/* 195 */     JarURLConnection.setUseFastExceptions(useFastConnectionExceptions);
/*     */   }
/*     */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\jar\Handler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */