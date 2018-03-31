/*    */ package org.springframework.boot.loader;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.jar.Attributes;
/*    */ import java.util.jar.Manifest;
/*    */ import org.springframework.boot.loader.archive.Archive;
/*    */ import org.springframework.boot.loader.archive.Archive.Entry;
/*    */ import org.springframework.boot.loader.archive.Archive.EntryFilter;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class ExecutableArchiveLauncher
/*    */   extends Launcher
/*    */ {
/*    */   private final Archive archive;
/*    */   
/*    */   public ExecutableArchiveLauncher()
/*    */   {
/*    */     try
/*    */     {
/* 40 */       this.archive = createArchive();
/*    */     }
/*    */     catch (Exception ex) {
/* 43 */       throw new IllegalStateException(ex);
/*    */     }
/*    */   }
/*    */   
/*    */   protected ExecutableArchiveLauncher(Archive archive) {
/* 48 */     this.archive = archive;
/*    */   }
/*    */   
/*    */   protected final Archive getArchive() {
/* 52 */     return this.archive;
/*    */   }
/*    */   
/*    */   protected String getMainClass() throws Exception
/*    */   {
/* 57 */     Manifest manifest = this.archive.getManifest();
/* 58 */     String mainClass = null;
/* 59 */     if (manifest != null) {
/* 60 */       mainClass = manifest.getMainAttributes().getValue("Start-Class");
/*    */     }
/* 62 */     if (mainClass == null) {
/* 63 */       throw new IllegalStateException("No 'Start-Class' manifest entry specified in " + this);
/*    */     }
/*    */     
/* 66 */     return mainClass;
/*    */   }
/*    */   
/*    */   protected List<Archive> getClassPathArchives()
/*    */     throws Exception
/*    */   {
/* 72 */     List<Archive> archives = new ArrayList(this.archive.getNestedArchives(new Archive.EntryFilter()
/*    */     {
/*    */       public boolean matches(Archive.Entry entry)
/*    */       {
/* 76 */         return ExecutableArchiveLauncher.this.isNestedArchive(entry);
/*    */       }
/*    */       
/* 79 */     }));
/* 80 */     postProcessClassPathArchives(archives);
/* 81 */     return archives;
/*    */   }
/*    */   
/*    */   protected abstract boolean isNestedArchive(Archive.Entry paramEntry);
/*    */   
/*    */   protected void postProcessClassPathArchives(List<Archive> archives)
/*    */     throws Exception
/*    */   {}
/*    */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\ExecutableArchiveLauncher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */