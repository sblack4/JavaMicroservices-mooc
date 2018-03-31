/*    */ package org.springframework.boot.loader;
/*    */ 
/*    */ import java.util.List;
/*    */ import org.springframework.boot.loader.archive.Archive;
/*    */ import org.springframework.boot.loader.archive.Archive.Entry;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JarLauncher
/*    */   extends ExecutableArchiveLauncher
/*    */ {
/*    */   static final String BOOT_INF_CLASSES = "BOOT-INF/classes/";
/*    */   static final String BOOT_INF_LIB = "BOOT-INF/lib/";
/*    */   
/*    */   public JarLauncher() {}
/*    */   
/*    */   protected JarLauncher(Archive archive)
/*    */   {
/* 41 */     super(archive);
/*    */   }
/*    */   
/*    */   protected boolean isNestedArchive(Archive.Entry entry)
/*    */   {
/* 46 */     if (entry.isDirectory()) {
/* 47 */       return entry.getName().equals("BOOT-INF/classes/");
/*    */     }
/* 49 */     return entry.getName().startsWith("BOOT-INF/lib/");
/*    */   }
/*    */   
/*    */   protected void postProcessClassPathArchives(List<Archive> archives) throws Exception
/*    */   {
/* 54 */     archives.add(0, getArchive());
/*    */   }
/*    */   
/*    */   public static void main(String[] args) throws Exception {
/* 58 */     new JarLauncher().launch(args);
/*    */   }
/*    */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\JarLauncher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */