/*     */ package org.springframework.boot.loader;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.JarURLConnection;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import java.net.URLConnection;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.Enumeration;
/*     */ import org.springframework.boot.loader.jar.Handler;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LaunchedURLClassLoader
/*     */   extends URLClassLoader
/*     */ {
/*     */   public LaunchedURLClassLoader(URL[] urls, ClassLoader parent)
/*     */   {
/*  46 */     super(urls, parent);
/*     */   }
/*     */   
/*     */   public URL findResource(String name)
/*     */   {
/*  51 */     Handler.setUseFastConnectionExceptions(true);
/*     */     try {
/*  53 */       return super.findResource(name);
/*     */     }
/*     */     finally {
/*  56 */       Handler.setUseFastConnectionExceptions(false);
/*     */     }
/*     */   }
/*     */   
/*     */   public Enumeration<URL> findResources(String name) throws IOException
/*     */   {
/*  62 */     Handler.setUseFastConnectionExceptions(true);
/*     */     try {
/*  64 */       return super.findResources(name);
/*     */     }
/*     */     finally {
/*  67 */       Handler.setUseFastConnectionExceptions(false);
/*     */     }
/*     */   }
/*     */   
/*     */   protected Class<?> loadClass(String name, boolean resolve)
/*     */     throws ClassNotFoundException
/*     */   {
/*  74 */     Handler.setUseFastConnectionExceptions(true);
/*     */     try {
/*     */       try {
/*  77 */         definePackageIfNecessary(name);
/*     */       }
/*     */       catch (IllegalArgumentException ex)
/*     */       {
/*  81 */         if (getPackage(name) == null)
/*     */         {
/*     */ 
/*     */ 
/*  85 */           throw new AssertionError("Package " + name + " has already been " + "defined but it could not be found");
/*     */         }
/*     */       }
/*     */       
/*  89 */       return super.loadClass(name, resolve);
/*     */     }
/*     */     finally {
/*  92 */       Handler.setUseFastConnectionExceptions(false);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void definePackageIfNecessary(String className)
/*     */   {
/* 103 */     int lastDot = className.lastIndexOf('.');
/* 104 */     if (lastDot >= 0) {
/* 105 */       String packageName = className.substring(0, lastDot);
/* 106 */       if (getPackage(packageName) == null) {
/*     */         try {
/* 108 */           definePackage(className, packageName);
/*     */         }
/*     */         catch (IllegalArgumentException ex)
/*     */         {
/* 112 */           if (getPackage(packageName) == null)
/*     */           {
/*     */ 
/*     */ 
/* 116 */             throw new AssertionError("Package " + packageName + " has already been defined " + "but it could not be found");
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void definePackage(final String className, final String packageName)
/*     */   {
/*     */     try
/*     */     {
/* 127 */       AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */       {
/*     */         public Object run() throws ClassNotFoundException {
/* 130 */           String packageEntryName = packageName.replace(".", "/") + "/";
/* 131 */           String classEntryName = className.replace(".", "/") + ".class";
/* 132 */           for (URL url : LaunchedURLClassLoader.this.getURLs()) {
/*     */             try {
/* 134 */               if ((url.getContent() instanceof JarFile)) {
/* 135 */                 JarFile jarFile = (JarFile)url.getContent();
/* 136 */                 if ((jarFile.getEntry(classEntryName) != null) && 
/* 137 */                   (jarFile.getEntry(packageEntryName) != null) && 
/* 138 */                   (jarFile.getManifest() != null)) {
/* 139 */                   LaunchedURLClassLoader.this.definePackage(packageName, jarFile.getManifest(), url);
/*     */                   
/* 141 */                   return null;
/*     */                 }
/*     */               }
/*     */             }
/*     */             catch (IOException localIOException) {}
/*     */           }
/*     */           
/*     */ 
/* 149 */           return null;
/*     */         }
/* 151 */       }, AccessController.getContext());
/*     */     }
/*     */     catch (PrivilegedActionException localPrivilegedActionException) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void clearCache()
/*     */   {
/* 162 */     for (URL url : getURLs()) {
/*     */       try {
/* 164 */         URLConnection connection = url.openConnection();
/* 165 */         if ((connection instanceof JarURLConnection)) {
/* 166 */           clearCache(connection);
/*     */         }
/*     */       }
/*     */       catch (IOException localIOException) {}
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void clearCache(URLConnection connection)
/*     */     throws IOException
/*     */   {
/* 177 */     Object jarFile = ((JarURLConnection)connection).getJarFile();
/* 178 */     if ((jarFile instanceof JarFile)) {
/* 179 */       ((JarFile)jarFile).clearCache();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\LaunchedURLClassLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */