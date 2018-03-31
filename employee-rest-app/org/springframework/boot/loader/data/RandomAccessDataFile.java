/*     */ package org.springframework.boot.loader.data;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import java.util.concurrent.Semaphore;
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
/*     */ public class RandomAccessDataFile
/*     */   implements RandomAccessData
/*     */ {
/*     */   private static final int DEFAULT_CONCURRENT_READS = 4;
/*     */   private final File file;
/*     */   private final FilePool filePool;
/*     */   private final long offset;
/*     */   private final long length;
/*     */   
/*     */   public RandomAccessDataFile(File file)
/*     */   {
/*  51 */     this(file, 4);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public RandomAccessDataFile(File file, int concurrentReads)
/*     */   {
/*  63 */     if (file == null) {
/*  64 */       throw new IllegalArgumentException("File must not be null");
/*     */     }
/*  66 */     if (!file.exists()) {
/*  67 */       throw new IllegalArgumentException("File must exist");
/*     */     }
/*  69 */     this.file = file;
/*  70 */     this.filePool = new FilePool(concurrentReads);
/*  71 */     this.offset = 0L;
/*  72 */     this.length = file.length();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private RandomAccessDataFile(File file, FilePool pool, long offset, long length)
/*     */   {
/*  83 */     this.file = file;
/*  84 */     this.filePool = pool;
/*  85 */     this.offset = offset;
/*  86 */     this.length = length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public File getFile()
/*     */   {
/*  94 */     return this.file;
/*     */   }
/*     */   
/*     */   public InputStream getInputStream(RandomAccessData.ResourceAccess access) throws IOException
/*     */   {
/*  99 */     return new DataInputStream(access);
/*     */   }
/*     */   
/*     */   public RandomAccessData getSubsection(long offset, long length)
/*     */   {
/* 104 */     if ((offset < 0L) || (length < 0L) || (offset + length > this.length)) {
/* 105 */       throw new IndexOutOfBoundsException();
/*     */     }
/* 107 */     return new RandomAccessDataFile(this.file, this.filePool, this.offset + offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSize()
/*     */   {
/* 113 */     return this.length;
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 117 */     this.filePool.close();
/*     */   }
/*     */   
/*     */ 
/*     */   private class DataInputStream
/*     */     extends InputStream
/*     */   {
/*     */     private RandomAccessFile file;
/*     */     
/*     */     private int position;
/*     */     
/*     */     DataInputStream(RandomAccessData.ResourceAccess access)
/*     */       throws IOException
/*     */     {
/* 131 */       if (access == RandomAccessData.ResourceAccess.ONCE) {
/* 132 */         this.file = new RandomAccessFile(RandomAccessDataFile.this.file, "r");
/* 133 */         this.file.seek(RandomAccessDataFile.this.offset);
/*     */       }
/*     */     }
/*     */     
/*     */     public int read() throws IOException
/*     */     {
/* 139 */       return doRead(null, 0, 1);
/*     */     }
/*     */     
/*     */     public int read(byte[] b) throws IOException
/*     */     {
/* 144 */       return read(b, 0, b == null ? 0 : b.length);
/*     */     }
/*     */     
/*     */     public int read(byte[] b, int off, int len) throws IOException
/*     */     {
/* 149 */       if (b == null) {
/* 150 */         throw new NullPointerException("Bytes must not be null");
/*     */       }
/* 152 */       return doRead(b, off, len);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public int doRead(byte[] b, int off, int len)
/*     */       throws IOException
/*     */     {
/* 165 */       if (len == 0) {
/* 166 */         return 0;
/*     */       }
/* 168 */       int cappedLen = cap(len);
/* 169 */       if (cappedLen <= 0) {
/* 170 */         return -1;
/*     */       }
/* 172 */       RandomAccessFile file = this.file;
/* 173 */       if (file == null) {
/* 174 */         file = RandomAccessDataFile.this.filePool.acquire();
/* 175 */         file.seek(RandomAccessDataFile.this.offset + this.position);
/*     */       }
/*     */       try { int rtn;
/* 178 */         if (b == null) {
/* 179 */           rtn = file.read();
/* 180 */           moveOn(rtn == -1 ? 0 : 1);
/* 181 */           return rtn;
/*     */         }
/*     */         
/* 184 */         return (int)moveOn(file.read(b, off, cappedLen));
/*     */       }
/*     */       finally
/*     */       {
/* 188 */         if (this.file == null) {
/* 189 */           RandomAccessDataFile.this.filePool.release(file);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     public long skip(long n) throws IOException
/*     */     {
/* 196 */       return n <= 0L ? 0L : moveOn(cap(n));
/*     */     }
/*     */     
/*     */     public void close() throws IOException
/*     */     {
/* 201 */       if (this.file != null) {
/* 202 */         this.file.close();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private int cap(long n)
/*     */     {
/* 213 */       return (int)Math.min(RandomAccessDataFile.this.length - this.position, n);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private long moveOn(int amount)
/*     */     {
/* 222 */       this.position += amount;
/* 223 */       return amount;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private class FilePool
/*     */   {
/*     */     private final int size;
/*     */     
/*     */ 
/*     */     private final Semaphore available;
/*     */     
/*     */     private final Queue<RandomAccessFile> files;
/*     */     
/*     */ 
/*     */     FilePool(int size)
/*     */     {
/* 241 */       this.size = size;
/* 242 */       this.available = new Semaphore(size);
/* 243 */       this.files = new ConcurrentLinkedQueue();
/*     */     }
/*     */     
/*     */     public RandomAccessFile acquire() throws IOException {
/*     */       try {
/* 248 */         this.available.acquire();
/* 249 */         RandomAccessFile file = (RandomAccessFile)this.files.poll();
/*     */         
/* 251 */         return file == null ? new RandomAccessFile(RandomAccessDataFile.this.file, "r") : file;
/*     */       }
/*     */       catch (InterruptedException ex)
/*     */       {
/* 255 */         Thread.currentThread().interrupt();
/* 256 */         throw new IOException(ex);
/*     */       }
/*     */     }
/*     */     
/*     */     public void release(RandomAccessFile file) {
/* 261 */       this.files.add(file);
/* 262 */       this.available.release();
/*     */     }
/*     */     
/*     */     /* Error */
/*     */     public void close()
/*     */       throws IOException
/*     */     {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: getfield 6	org/springframework/boot/loader/data/RandomAccessDataFile$FilePool:available	Ljava/util/concurrent/Semaphore;
/*     */       //   4: aload_0
/*     */       //   5: getfield 3	org/springframework/boot/loader/data/RandomAccessDataFile$FilePool:size	I
/*     */       //   8: invokevirtual 23	java/util/concurrent/Semaphore:acquire	(I)V
/*     */       //   11: aload_0
/*     */       //   12: getfield 9	org/springframework/boot/loader/data/RandomAccessDataFile$FilePool:files	Ljava/util/Queue;
/*     */       //   15: invokeinterface 11 1 0
/*     */       //   20: checkcast 12	java/io/RandomAccessFile
/*     */       //   23: astore_1
/*     */       //   24: aload_1
/*     */       //   25: ifnull +23 -> 48
/*     */       //   28: aload_1
/*     */       //   29: invokevirtual 24	java/io/RandomAccessFile:close	()V
/*     */       //   32: aload_0
/*     */       //   33: getfield 9	org/springframework/boot/loader/data/RandomAccessDataFile$FilePool:files	Ljava/util/Queue;
/*     */       //   36: invokeinterface 11 1 0
/*     */       //   41: checkcast 12	java/io/RandomAccessFile
/*     */       //   44: astore_1
/*     */       //   45: goto -21 -> 24
/*     */       //   48: aload_0
/*     */       //   49: getfield 6	org/springframework/boot/loader/data/RandomAccessDataFile$FilePool:available	Ljava/util/concurrent/Semaphore;
/*     */       //   52: aload_0
/*     */       //   53: getfield 3	org/springframework/boot/loader/data/RandomAccessDataFile$FilePool:size	I
/*     */       //   56: invokevirtual 25	java/util/concurrent/Semaphore:release	(I)V
/*     */       //   59: goto +17 -> 76
/*     */       //   62: astore_2
/*     */       //   63: aload_0
/*     */       //   64: getfield 6	org/springframework/boot/loader/data/RandomAccessDataFile$FilePool:available	Ljava/util/concurrent/Semaphore;
/*     */       //   67: aload_0
/*     */       //   68: getfield 3	org/springframework/boot/loader/data/RandomAccessDataFile$FilePool:size	I
/*     */       //   71: invokevirtual 25	java/util/concurrent/Semaphore:release	(I)V
/*     */       //   74: aload_2
/*     */       //   75: athrow
/*     */       //   76: goto +19 -> 95
/*     */       //   79: astore_1
/*     */       //   80: invokestatic 17	java/lang/Thread:currentThread	()Ljava/lang/Thread;
/*     */       //   83: invokevirtual 18	java/lang/Thread:interrupt	()V
/*     */       //   86: new 19	java/io/IOException
/*     */       //   89: dup
/*     */       //   90: aload_1
/*     */       //   91: invokespecial 20	java/io/IOException:<init>	(Ljava/lang/Throwable;)V
/*     */       //   94: athrow
/*     */       //   95: return
/*     */       // Line number table:
/*     */       //   Java source line #267	-> byte code offset #0
/*     */       //   Java source line #269	-> byte code offset #11
/*     */       //   Java source line #270	-> byte code offset #24
/*     */       //   Java source line #271	-> byte code offset #28
/*     */       //   Java source line #272	-> byte code offset #32
/*     */       //   Java source line #276	-> byte code offset #48
/*     */       //   Java source line #277	-> byte code offset #59
/*     */       //   Java source line #276	-> byte code offset #62
/*     */       //   Java source line #282	-> byte code offset #76
/*     */       //   Java source line #279	-> byte code offset #79
/*     */       //   Java source line #280	-> byte code offset #80
/*     */       //   Java source line #281	-> byte code offset #86
/*     */       //   Java source line #283	-> byte code offset #95
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	96	0	this	FilePool
/*     */       //   23	22	1	file	RandomAccessFile
/*     */       //   79	12	1	ex	InterruptedException
/*     */       //   62	13	2	localObject	Object
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   11	48	62	finally
/*     */       //   0	76	79	java/lang/InterruptedException
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\data\RandomAccessDataFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */