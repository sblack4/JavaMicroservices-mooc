/*     */ package org.springframework.boot.loader;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.jar.Attributes;
/*     */ import java.util.jar.Manifest;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.springframework.boot.loader.archive.Archive;
/*     */ import org.springframework.boot.loader.archive.Archive.Entry;
/*     */ import org.springframework.boot.loader.archive.Archive.EntryFilter;
/*     */ import org.springframework.boot.loader.archive.ExplodedArchive;
/*     */ import org.springframework.boot.loader.archive.JarFileArchive;
/*     */ import org.springframework.boot.loader.util.SystemPropertyUtils;
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
/*     */ public class PropertiesLauncher
/*     */   extends Launcher
/*     */ {
/*     */   private static final String DEBUG = "loader.debug";
/*     */   public static final String MAIN = "loader.main";
/*     */   public static final String PATH = "loader.path";
/*     */   public static final String HOME = "loader.home";
/*     */   public static final String ARGS = "loader.args";
/*     */   public static final String CONFIG_NAME = "loader.config.name";
/*     */   public static final String CONFIG_LOCATION = "loader.config.location";
/*     */   public static final String SET_SYSTEM_PROPERTIES = "loader.system";
/* 122 */   private static final Pattern WORD_SEPARATOR = Pattern.compile("\\W+");
/*     */   
/*     */   private final File home;
/*     */   
/* 126 */   private List<String> paths = new ArrayList();
/*     */   
/* 128 */   private final Properties properties = new Properties();
/*     */   private Archive parent;
/*     */   
/*     */   public PropertiesLauncher()
/*     */   {
/*     */     try {
/* 134 */       this.home = getHomeDirectory();
/* 135 */       initializeProperties(this.home);
/* 136 */       initializePaths();
/* 137 */       this.parent = createArchive();
/*     */     }
/*     */     catch (Exception ex) {
/* 140 */       throw new IllegalStateException(ex);
/*     */     }
/*     */   }
/*     */   
/*     */   protected File getHomeDirectory()
/*     */   {
/* 146 */     return new File(SystemPropertyUtils.resolvePlaceholders(System.getProperty("loader.home", "${user.dir}")));
/*     */   }
/*     */   
/*     */   private void initializeProperties(File home) throws Exception, IOException
/*     */   {
/* 151 */     String config = "classpath:BOOT-INF/classes/" + SystemPropertyUtils.resolvePlaceholders(
/* 152 */       SystemPropertyUtils.getProperty("loader.config.name", "application")) + ".properties";
/*     */     
/* 154 */     config = SystemPropertyUtils.resolvePlaceholders(
/* 155 */       SystemPropertyUtils.getProperty("loader.config.location", config));
/* 156 */     InputStream resource = getResource(config);
/* 157 */     if (resource != null) {
/* 158 */       log("Found: " + config);
/*     */       try {
/* 160 */         this.properties.load(resource);
/*     */       }
/*     */       finally {
/* 163 */         resource.close();
/*     */       }
/* 165 */       for (Object key : Collections.list(this.properties.propertyNames())) {
/* 166 */         String text = this.properties.getProperty((String)key);
/* 167 */         String value = SystemPropertyUtils.resolvePlaceholders(this.properties, text);
/*     */         
/* 169 */         if (value != null) {
/* 170 */           this.properties.put(key, value);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 175 */       if (SystemPropertyUtils.resolvePlaceholders("${loader.system:false}").equals("true")) {
/* 176 */         log("Adding resolved properties to System properties");
/* 177 */         for (Object key : Collections.list(this.properties.propertyNames())) {
/* 178 */           String value = this.properties.getProperty((String)key);
/* 179 */           System.setProperty((String)key, value);
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 184 */       log("Not found: " + config);
/*     */     }
/*     */   }
/*     */   
/*     */   private InputStream getResource(String config) throws Exception
/*     */   {
/* 190 */     if (config.startsWith("classpath:")) {
/* 191 */       return getClasspathResource(config.substring("classpath:".length()));
/*     */     }
/* 193 */     config = stripFileUrlPrefix(config);
/* 194 */     if (isUrl(config)) {
/* 195 */       return getURLResource(config);
/*     */     }
/* 197 */     return getFileResource(config);
/*     */   }
/*     */   
/*     */   private String stripFileUrlPrefix(String config) {
/* 201 */     if (config.startsWith("file:")) {
/* 202 */       config = config.substring("file:".length());
/* 203 */       if (config.startsWith("//")) {
/* 204 */         config = config.substring(2);
/*     */       }
/*     */     }
/* 207 */     return config;
/*     */   }
/*     */   
/*     */   private boolean isUrl(String config) {
/* 211 */     return config.contains("://");
/*     */   }
/*     */   
/*     */   private InputStream getClasspathResource(String config) {
/* 215 */     while (config.startsWith("/")) {
/* 216 */       config = config.substring(1);
/*     */     }
/* 218 */     config = "/" + config;
/* 219 */     log("Trying classpath: " + config);
/* 220 */     return getClass().getResourceAsStream(config);
/*     */   }
/*     */   
/*     */   private InputStream getFileResource(String config) throws Exception {
/* 224 */     File file = new File(config);
/* 225 */     log("Trying file: " + config);
/* 226 */     if (file.canRead()) {
/* 227 */       return new FileInputStream(file);
/*     */     }
/* 229 */     return null;
/*     */   }
/*     */   
/*     */   private InputStream getURLResource(String config) throws Exception {
/* 233 */     URL url = new URL(config);
/* 234 */     if (exists(url)) {
/* 235 */       URLConnection con = url.openConnection();
/*     */       try {
/* 237 */         return con.getInputStream();
/*     */       }
/*     */       catch (IOException ex)
/*     */       {
/* 241 */         if ((con instanceof HttpURLConnection)) {
/* 242 */           ((HttpURLConnection)con).disconnect();
/*     */         }
/* 244 */         throw ex;
/*     */       }
/*     */     }
/* 247 */     return null;
/*     */   }
/*     */   
/*     */   private boolean exists(URL url) throws IOException
/*     */   {
/* 252 */     URLConnection connection = url.openConnection();
/*     */     try {
/* 254 */       connection.setUseCaches(connection
/* 255 */         .getClass().getSimpleName().startsWith("JNLP"));
/* 256 */       HttpURLConnection httpConnection; if ((connection instanceof HttpURLConnection)) {
/* 257 */         httpConnection = (HttpURLConnection)connection;
/* 258 */         httpConnection.setRequestMethod("HEAD");
/* 259 */         int responseCode = httpConnection.getResponseCode();
/* 260 */         boolean bool; if (responseCode == 200) {
/* 261 */           return true;
/*     */         }
/* 263 */         if (responseCode == 404) {
/* 264 */           return false;
/*     */         }
/*     */       }
/* 267 */       return connection.getContentLength() >= 0 ? 1 : 0;
/*     */     }
/*     */     finally {
/* 270 */       if ((connection instanceof HttpURLConnection)) {
/* 271 */         ((HttpURLConnection)connection).disconnect();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void initializePaths() throws IOException {
/* 277 */     String path = SystemPropertyUtils.getProperty("loader.path");
/* 278 */     if (path == null) {
/* 279 */       path = this.properties.getProperty("loader.path");
/*     */     }
/* 281 */     if (path != null) {
/* 282 */       this.paths = parsePathsProperty(
/* 283 */         SystemPropertyUtils.resolvePlaceholders(path));
/*     */     }
/* 285 */     log("Nested archive paths: " + this.paths);
/*     */   }
/*     */   
/*     */   private List<String> parsePathsProperty(String commaSeparatedPaths) {
/* 289 */     List<String> paths = new ArrayList();
/* 290 */     for (String path : commaSeparatedPaths.split(",")) {
/* 291 */       path = cleanupPath(path);
/*     */       
/*     */ 
/* 294 */       if (!path.equals("")) {
/* 295 */         paths.add(path);
/*     */       }
/*     */     }
/* 298 */     if (paths.isEmpty()) {
/* 299 */       paths.add("lib");
/*     */     }
/* 301 */     return paths;
/*     */   }
/*     */   
/*     */   protected String[] getArgs(String... args) throws Exception {
/* 305 */     String loaderArgs = getProperty("loader.args");
/* 306 */     if (loaderArgs != null) {
/* 307 */       String[] defaultArgs = loaderArgs.split("\\s+");
/* 308 */       String[] additionalArgs = args;
/* 309 */       args = new String[defaultArgs.length + additionalArgs.length];
/* 310 */       System.arraycopy(defaultArgs, 0, args, 0, defaultArgs.length);
/* 311 */       System.arraycopy(additionalArgs, 0, args, defaultArgs.length, additionalArgs.length);
/*     */     }
/*     */     
/* 314 */     return args;
/*     */   }
/*     */   
/*     */   protected String getMainClass() throws Exception
/*     */   {
/* 319 */     String mainClass = getProperty("loader.main", "Start-Class");
/* 320 */     if (mainClass == null) {
/* 321 */       throw new IllegalStateException("No 'loader.main' or 'Start-Class' specified");
/*     */     }
/*     */     
/* 324 */     return mainClass;
/*     */   }
/*     */   
/*     */   protected ClassLoader createClassLoader(List<Archive> archives) throws Exception
/*     */   {
/* 329 */     ClassLoader loader = super.createClassLoader(archives);
/* 330 */     String customLoaderClassName = getProperty("loader.classLoader");
/* 331 */     if (customLoaderClassName != null) {
/* 332 */       loader = wrapWithCustomClassLoader(loader, customLoaderClassName);
/* 333 */       log("Using custom class loader: " + customLoaderClassName);
/*     */     }
/* 335 */     return loader;
/*     */   }
/*     */   
/*     */ 
/*     */   private ClassLoader wrapWithCustomClassLoader(ClassLoader parent, String loaderClassName)
/*     */     throws Exception
/*     */   {
/* 342 */     Class<ClassLoader> loaderClass = Class.forName(loaderClassName, true, parent);
/*     */     try
/*     */     {
/* 345 */       return (ClassLoader)loaderClass.getConstructor(new Class[] { ClassLoader.class }).newInstance(new Object[] { parent });
/*     */ 
/*     */     }
/*     */     catch (NoSuchMethodException localNoSuchMethodException)
/*     */     {
/*     */       try
/*     */       {
/* 352 */         return (ClassLoader)loaderClass.getConstructor(new Class[] { URL[].class, ClassLoader.class }).newInstance(new Object[] { new URL[0], parent });
/*     */       }
/*     */       catch (NoSuchMethodException localNoSuchMethodException1) {}
/*     */     }
/*     */     
/* 357 */     return (ClassLoader)loaderClass.newInstance();
/*     */   }
/*     */   
/*     */   private String getProperty(String propertyKey) throws Exception {
/* 361 */     return getProperty(propertyKey, null);
/*     */   }
/*     */   
/*     */   private String getProperty(String propertyKey, String manifestKey) throws Exception {
/* 365 */     if (manifestKey == null) {
/* 366 */       manifestKey = propertyKey.replace(".", "-");
/* 367 */       manifestKey = toCamelCase(manifestKey);
/*     */     }
/* 369 */     String property = SystemPropertyUtils.getProperty(propertyKey);
/* 370 */     if (property != null) {
/* 371 */       String value = SystemPropertyUtils.resolvePlaceholders(property);
/* 372 */       log("Property '" + propertyKey + "' from environment: " + value);
/* 373 */       return value;
/*     */     }
/* 375 */     if (this.properties.containsKey(propertyKey))
/*     */     {
/* 377 */       String value = SystemPropertyUtils.resolvePlaceholders(this.properties.getProperty(propertyKey));
/* 378 */       log("Property '" + propertyKey + "' from properties: " + value);
/* 379 */       return value;
/*     */     }
/*     */     try
/*     */     {
/* 383 */       Manifest manifest = new ExplodedArchive(this.home, false).getManifest();
/* 384 */       if (manifest != null) {
/* 385 */         String value = manifest.getMainAttributes().getValue(manifestKey);
/* 386 */         log("Property '" + manifestKey + "' from home directory manifest: " + value);
/*     */         
/* 388 */         return value;
/*     */       }
/*     */     }
/*     */     catch (IllegalStateException localIllegalStateException) {}
/*     */     
/*     */ 
/*     */ 
/* 395 */     Manifest manifest = createArchive().getManifest();
/* 396 */     if (manifest != null) {
/* 397 */       String value = manifest.getMainAttributes().getValue(manifestKey);
/* 398 */       if (value != null) {
/* 399 */         log("Property '" + manifestKey + "' from archive manifest: " + value);
/* 400 */         return value;
/*     */       }
/*     */     }
/* 403 */     return null;
/*     */   }
/*     */   
/*     */   protected List<Archive> getClassPathArchives() throws Exception
/*     */   {
/* 408 */     List<Archive> lib = new ArrayList();
/* 409 */     for (String path : this.paths) {
/* 410 */       for (Archive archive : getClassPathArchives(path)) {
/* 411 */         if ((archive instanceof ExplodedArchive))
/*     */         {
/* 413 */           List<Archive> nested = new ArrayList(archive.getNestedArchives(new ArchiveEntryFilter(null)));
/* 414 */           nested.add(0, archive);
/* 415 */           lib.addAll(nested);
/*     */         }
/*     */         else {
/* 418 */           lib.add(archive);
/*     */         }
/*     */       }
/*     */     }
/* 422 */     addNestedEntries(lib);
/* 423 */     return lib;
/*     */   }
/*     */   
/*     */   private List<Archive> getClassPathArchives(String path) throws Exception {
/* 427 */     String root = cleanupPath(stripFileUrlPrefix(path));
/* 428 */     List<Archive> lib = new ArrayList();
/* 429 */     File file = new File(root);
/* 430 */     if (!isAbsolutePath(root)) {
/* 431 */       file = new File(this.home, root);
/*     */     }
/* 433 */     if (file.isDirectory()) {
/* 434 */       log("Adding classpath entries from " + file);
/* 435 */       Archive archive = new ExplodedArchive(file, false);
/* 436 */       lib.add(archive);
/*     */     }
/* 438 */     Archive archive = getArchive(file);
/* 439 */     if (archive != null) {
/* 440 */       log("Adding classpath entries from archive " + archive.getUrl() + root);
/* 441 */       lib.add(archive);
/*     */     }
/* 443 */     Archive nested = getNestedArchive(root);
/* 444 */     if (nested != null) {
/* 445 */       log("Adding classpath entries from nested " + nested.getUrl() + root);
/* 446 */       lib.add(nested);
/*     */     }
/* 448 */     return lib;
/*     */   }
/*     */   
/*     */   private boolean isAbsolutePath(String root)
/*     */   {
/* 453 */     return (root.contains(":")) || (root.startsWith("/"));
/*     */   }
/*     */   
/*     */   private Archive getArchive(File file) throws IOException {
/* 457 */     String name = file.getName().toLowerCase();
/* 458 */     if ((name.endsWith(".jar")) || (name.endsWith(".zip"))) {
/* 459 */       return new JarFileArchive(file);
/*     */     }
/* 461 */     return null;
/*     */   }
/*     */   
/*     */   private Archive getNestedArchive(String root) throws Exception {
/* 465 */     if ((root.startsWith("/")) || 
/* 466 */       (this.parent.getUrl().equals(this.home.toURI().toURL())))
/*     */     {
/* 468 */       return null;
/*     */     }
/* 470 */     Archive.EntryFilter filter = new PrefixMatchingArchiveFilter(root, null);
/* 471 */     if (this.parent.getNestedArchives(filter).isEmpty()) {
/* 472 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 476 */     return new FilteredArchive(this.parent, filter);
/*     */   }
/*     */   
/*     */ 
/*     */   private void addNestedEntries(List<Archive> lib)
/*     */   {
/*     */     try
/*     */     {
/* 484 */       lib.addAll(this.parent.getNestedArchives(new Archive.EntryFilter()
/*     */       {
/*     */         public boolean matches(Archive.Entry entry)
/*     */         {
/* 488 */           if (entry.isDirectory()) {
/* 489 */             return entry.getName().startsWith("BOOT-INF/classes/");
/*     */           }
/* 491 */           return entry.getName().startsWith("BOOT-INF/lib/");
/*     */         }
/*     */       }));
/*     */     }
/*     */     catch (IOException localIOException) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private String cleanupPath(String path)
/*     */   {
/* 502 */     path = path.trim();
/*     */     
/* 504 */     if (path.startsWith("./")) {
/* 505 */       path = path.substring(2);
/*     */     }
/* 507 */     if ((path.toLowerCase().endsWith(".jar")) || (path.toLowerCase().endsWith(".zip"))) {
/* 508 */       return path;
/*     */     }
/* 510 */     if (path.endsWith("/*")) {
/* 511 */       path = path.substring(0, path.length() - 1);
/*     */ 
/*     */ 
/*     */     }
/* 515 */     else if ((!path.endsWith("/")) && (!path.equals("."))) {
/* 516 */       path = path + "/";
/*     */     }
/*     */     
/* 519 */     return path;
/*     */   }
/*     */   
/*     */   public static void main(String[] args) throws Exception {
/* 523 */     PropertiesLauncher launcher = new PropertiesLauncher();
/* 524 */     args = launcher.getArgs(args);
/* 525 */     launcher.launch(args);
/*     */   }
/*     */   
/*     */   public static String toCamelCase(CharSequence string) {
/* 529 */     if (string == null) {
/* 530 */       return null;
/*     */     }
/* 532 */     StringBuilder builder = new StringBuilder();
/* 533 */     Matcher matcher = WORD_SEPARATOR.matcher(string);
/* 534 */     int pos = 0;
/* 535 */     while (matcher.find()) {
/* 536 */       builder.append(capitalize(string.subSequence(pos, matcher.end()).toString()));
/* 537 */       pos = matcher.end();
/*     */     }
/* 539 */     builder.append(capitalize(string.subSequence(pos, string.length()).toString()));
/* 540 */     return builder.toString();
/*     */   }
/*     */   
/*     */   private static Object capitalize(String str) {
/* 544 */     StringBuilder sb = new StringBuilder(str.length());
/* 545 */     sb.append(Character.toUpperCase(str.charAt(0)));
/* 546 */     sb.append(str.substring(1));
/* 547 */     return sb.toString();
/*     */   }
/*     */   
/*     */   private void log(String message) {
/* 551 */     if (Boolean.getBoolean("loader.debug"))
/*     */     {
/*     */ 
/* 554 */       System.out.println(message);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static final class PrefixMatchingArchiveFilter
/*     */     implements Archive.EntryFilter
/*     */   {
/*     */     private final String prefix;
/*     */     
/*     */ 
/* 566 */     private final PropertiesLauncher.ArchiveEntryFilter filter = new PropertiesLauncher.ArchiveEntryFilter(null);
/*     */     
/*     */     private PrefixMatchingArchiveFilter(String prefix) {
/* 569 */       this.prefix = prefix;
/*     */     }
/*     */     
/*     */     public boolean matches(Archive.Entry entry)
/*     */     {
/* 574 */       return (entry.getName().startsWith(this.prefix)) && (this.filter.matches(entry));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static final class ArchiveEntryFilter
/*     */     implements Archive.EntryFilter
/*     */   {
/*     */     private static final String DOT_JAR = ".jar";
/*     */     
/*     */ 
/*     */     private static final String DOT_ZIP = ".zip";
/*     */     
/*     */ 
/*     */     public boolean matches(Archive.Entry entry)
/*     */     {
/* 591 */       return (entry.getName().endsWith(".jar")) || (entry.getName().endsWith(".zip"));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static class FilteredArchive
/*     */     implements Archive
/*     */   {
/*     */     private final Archive parent;
/*     */     
/*     */     private final Archive.EntryFilter filter;
/*     */     
/*     */ 
/*     */     FilteredArchive(Archive parent, Archive.EntryFilter filter)
/*     */     {
/* 606 */       this.parent = parent;
/* 607 */       this.filter = filter;
/*     */     }
/*     */     
/*     */     public URL getUrl() throws MalformedURLException
/*     */     {
/* 612 */       return this.parent.getUrl();
/*     */     }
/*     */     
/*     */     public Manifest getManifest() throws IOException
/*     */     {
/* 617 */       return this.parent.getManifest();
/*     */     }
/*     */     
/*     */     public Iterator<Archive.Entry> iterator()
/*     */     {
/* 622 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */     public List<Archive> getNestedArchives(final Archive.EntryFilter filter)
/*     */       throws IOException
/*     */     {
/* 628 */       this.parent.getNestedArchives(new Archive.EntryFilter()
/*     */       {
/*     */         public boolean matches(Archive.Entry entry)
/*     */         {
/* 632 */           return (PropertiesLauncher.FilteredArchive.this.filter.matches(entry)) && (filter.matches(entry));
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\PropertiesLauncher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */