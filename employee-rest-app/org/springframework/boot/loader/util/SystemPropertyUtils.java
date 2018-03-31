/*     */ package org.springframework.boot.loader.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashSet;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class SystemPropertyUtils
/*     */ {
/*     */   public static final String PLACEHOLDER_PREFIX = "${";
/*     */   public static final String PLACEHOLDER_SUFFIX = "}";
/*     */   public static final String VALUE_SEPARATOR = ":";
/*  55 */   private static final String SIMPLE_PREFIX = "${".substring(1);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String resolvePlaceholders(String text)
/*     */   {
/*  67 */     if (text == null) {
/*  68 */       return text;
/*     */     }
/*  70 */     return parseStringValue(null, text, text, new HashSet());
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
/*     */ 
/*     */   public static String resolvePlaceholders(Properties properties, String text)
/*     */   {
/*  84 */     if (text == null) {
/*  85 */       return text;
/*     */     }
/*  87 */     return parseStringValue(properties, text, text, new HashSet());
/*     */   }
/*     */   
/*     */ 
/*     */   private static String parseStringValue(Properties properties, String value, String current, Set<String> visitedPlaceholders)
/*     */   {
/*  93 */     StringBuilder buf = new StringBuilder(current);
/*     */     
/*  95 */     int startIndex = current.indexOf("${");
/*  96 */     while (startIndex != -1) {
/*  97 */       int endIndex = findPlaceholderEndIndex(buf, startIndex);
/*  98 */       if (endIndex != -1)
/*     */       {
/* 100 */         String placeholder = buf.substring(startIndex + "${".length(), endIndex);
/* 101 */         String originalPlaceholder = placeholder;
/* 102 */         if (!visitedPlaceholders.add(originalPlaceholder)) {
/* 103 */           throw new IllegalArgumentException("Circular placeholder reference '" + originalPlaceholder + "' in property definitions");
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 109 */         placeholder = parseStringValue(properties, value, placeholder, visitedPlaceholders);
/*     */         
/*     */ 
/* 112 */         String propVal = resolvePlaceholder(properties, value, placeholder);
/* 113 */         if ((propVal == null) && (":" != null)) {
/* 114 */           int separatorIndex = placeholder.indexOf(":");
/* 115 */           if (separatorIndex != -1) {
/* 116 */             String actualPlaceholder = placeholder.substring(0, separatorIndex);
/*     */             
/*     */ 
/* 119 */             String defaultValue = placeholder.substring(separatorIndex + ":".length());
/* 120 */             propVal = resolvePlaceholder(properties, value, actualPlaceholder);
/*     */             
/* 122 */             if (propVal == null) {
/* 123 */               propVal = defaultValue;
/*     */             }
/*     */           }
/*     */         }
/* 127 */         if (propVal != null)
/*     */         {
/*     */ 
/* 130 */           propVal = parseStringValue(properties, value, propVal, visitedPlaceholders);
/*     */           
/* 132 */           buf.replace(startIndex, endIndex + "}".length(), propVal);
/*     */           
/* 134 */           startIndex = buf.indexOf("${", startIndex + propVal
/* 135 */             .length());
/*     */         }
/*     */         else
/*     */         {
/* 139 */           startIndex = buf.indexOf("${", endIndex + "}"
/* 140 */             .length());
/*     */         }
/* 142 */         visitedPlaceholders.remove(originalPlaceholder);
/*     */       }
/*     */       else {
/* 145 */         startIndex = -1;
/*     */       }
/*     */     }
/*     */     
/* 149 */     return buf.toString();
/*     */   }
/*     */   
/*     */   private static String resolvePlaceholder(Properties properties, String text, String placeholderName)
/*     */   {
/* 154 */     String propVal = getProperty(placeholderName, null, text);
/* 155 */     if (propVal != null) {
/* 156 */       return propVal;
/*     */     }
/* 158 */     return properties == null ? null : properties.getProperty(placeholderName);
/*     */   }
/*     */   
/*     */   public static String getProperty(String key) {
/* 162 */     return getProperty(key, null, "");
/*     */   }
/*     */   
/*     */   public static String getProperty(String key, String defaultValue) {
/* 166 */     return getProperty(key, defaultValue, "");
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
/*     */   public static String getProperty(String key, String defaultValue, String text)
/*     */   {
/*     */     try
/*     */     {
/* 181 */       String propVal = System.getProperty(key);
/* 182 */       if (propVal == null)
/*     */       {
/* 184 */         propVal = System.getenv(key);
/*     */       }
/* 186 */       if (propVal == null)
/*     */       {
/* 188 */         propVal = System.getenv(key.replace(".", "_"));
/*     */       }
/* 190 */       if (propVal == null)
/*     */       {
/* 192 */         propVal = System.getenv(key.toUpperCase().replace(".", "_"));
/*     */       }
/* 194 */       if (propVal != null) {
/* 195 */         return propVal;
/*     */       }
/*     */     }
/*     */     catch (Throwable ex) {
/* 199 */       System.err.println("Could not resolve key '" + key + "' in '" + text + "' as system property or in environment: " + ex);
/*     */     }
/*     */     
/* 202 */     return defaultValue;
/*     */   }
/*     */   
/*     */   private static int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
/* 206 */     int index = startIndex + "${".length();
/* 207 */     int withinNestedPlaceholder = 0;
/* 208 */     while (index < buf.length()) {
/* 209 */       if (substringMatch(buf, index, "}")) {
/* 210 */         if (withinNestedPlaceholder > 0) {
/* 211 */           withinNestedPlaceholder--;
/* 212 */           index += "}".length();
/*     */         }
/*     */         else {
/* 215 */           return index;
/*     */         }
/*     */       }
/* 218 */       else if (substringMatch(buf, index, SIMPLE_PREFIX)) {
/* 219 */         withinNestedPlaceholder++;
/* 220 */         index += SIMPLE_PREFIX.length();
/*     */       }
/*     */       else {
/* 223 */         index++;
/*     */       }
/*     */     }
/* 226 */     return -1;
/*     */   }
/*     */   
/*     */   private static boolean substringMatch(CharSequence str, int index, CharSequence substring)
/*     */   {
/* 231 */     for (int j = 0; j < substring.length(); j++) {
/* 232 */       int i = index + j;
/* 233 */       if ((i >= str.length()) || (str.charAt(i) != substring.charAt(j))) {
/* 234 */         return false;
/*     */       }
/*     */     }
/* 237 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\util\SystemPropertyUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */