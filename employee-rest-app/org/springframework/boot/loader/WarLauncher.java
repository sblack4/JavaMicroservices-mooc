/*    */ package org.springframework.boot.loader;
/*    */ 
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
/*    */ 
/*    */ 
/*    */ public class WarLauncher
/*    */   extends ExecutableArchiveLauncher
/*    */ {
/*    */   private static final String WEB_INF = "WEB-INF/";
/*    */   private static final String WEB_INF_CLASSES = "WEB-INF/classes/";
/*    */   private static final String WEB_INF_LIB = "WEB-INF/lib/";
/*    */   private static final String WEB_INF_LIB_PROVIDED = "WEB-INF/lib-provided/";
/*    */   
/*    */   public WarLauncher() {}
/*    */   
/*    */   protected WarLauncher(Archive archive)
/*    */   {
/* 44 */     super(archive);
/*    */   }
/*    */   
/*    */   public boolean isNestedArchive(Archive.Entry entry)
/*    */   {
/* 49 */     if (entry.isDirectory()) {
/* 50 */       return entry.getName().equals("WEB-INF/classes/");
/*    */     }
/*    */     
/*    */ 
/* 54 */     return (entry.getName().startsWith("WEB-INF/lib/")) || (entry.getName().startsWith("WEB-INF/lib-provided/"));
/*    */   }
/*    */   
/*    */   public static void main(String[] args) throws Exception
/*    */   {
/* 59 */     new WarLauncher().launch(args);
/*    */   }
/*    */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\WarLauncher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */