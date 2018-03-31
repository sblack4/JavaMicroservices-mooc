/*     */ package org.springframework.boot.loader.jar;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NoSuchElementException;
/*     */ import org.springframework.boot.loader.data.RandomAccessData;
/*     */ import org.springframework.boot.loader.data.RandomAccessData.ResourceAccess;
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
/*     */ class JarFileEntries
/*     */   implements CentralDirectoryVisitor, Iterable<JarEntry>
/*     */ {
/*     */   private static final long LOCAL_FILE_HEADER_SIZE = 30L;
/*     */   private static final String SLASH = "/";
/*     */   private static final String NO_SUFFIX = "";
/*     */   protected static final int ENTRY_CACHE_SIZE = 25;
/*     */   private final JarFile jarFile;
/*     */   private final JarEntryFilter filter;
/*     */   private RandomAccessData centralDirectoryData;
/*     */   private int size;
/*     */   private int[] hashCodes;
/*     */   private int[] centralDirectoryOffsets;
/*     */   private int[] positions;
/*  70 */   private final Map<Integer, FileHeader> entriesCache = Collections.synchronizedMap(new LinkedHashMap(16, 0.75F, true)
/*     */   {
/*     */ 
/*     */     protected boolean removeEldestEntry(Map.Entry<Integer, FileHeader> eldest)
/*     */     {
/*     */ 
/*  75 */       if (JarFileEntries.this.jarFile.isSigned()) {
/*  76 */         return false;
/*     */       }
/*  78 */       return size() >= 25;
/*     */     }
/*  70 */   });
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
/*     */   JarFileEntries(JarFile jarFile, JarEntryFilter filter)
/*     */   {
/*  84 */     this.jarFile = jarFile;
/*  85 */     this.filter = filter;
/*     */   }
/*     */   
/*     */ 
/*     */   public void visitStart(CentralDirectoryEndRecord endRecord, RandomAccessData centralDirectoryData)
/*     */   {
/*  91 */     int maxSize = endRecord.getNumberOfRecords();
/*  92 */     this.centralDirectoryData = centralDirectoryData;
/*  93 */     this.hashCodes = new int[maxSize];
/*  94 */     this.centralDirectoryOffsets = new int[maxSize];
/*  95 */     this.positions = new int[maxSize];
/*     */   }
/*     */   
/*     */   public void visitFileHeader(CentralDirectoryFileHeader fileHeader, int dataOffset)
/*     */   {
/* 100 */     AsciiBytes name = applyFilter(fileHeader.getName());
/* 101 */     if (name != null) {
/* 102 */       add(name, fileHeader, dataOffset);
/*     */     }
/*     */   }
/*     */   
/*     */   private void add(AsciiBytes name, CentralDirectoryFileHeader fileHeader, int dataOffset)
/*     */   {
/* 108 */     this.hashCodes[this.size] = name.hashCode();
/* 109 */     this.centralDirectoryOffsets[this.size] = dataOffset;
/* 110 */     this.positions[this.size] = this.size;
/* 111 */     this.size += 1;
/*     */   }
/*     */   
/*     */   public void visitEnd()
/*     */   {
/* 116 */     sort(0, this.size - 1);
/* 117 */     int[] positions = this.positions;
/* 118 */     this.positions = new int[positions.length];
/* 119 */     for (int i = 0; i < this.size; i++) {
/* 120 */       this.positions[positions[i]] = i;
/*     */     }
/*     */   }
/*     */   
/*     */   private void sort(int left, int right)
/*     */   {
/* 126 */     if (left < right) {
/* 127 */       int pivot = this.hashCodes[(left + (right - left) / 2)];
/* 128 */       int i = left;
/* 129 */       int j = right;
/* 130 */       while (i <= j) {
/* 131 */         while (this.hashCodes[i] < pivot) {
/* 132 */           i++;
/*     */         }
/* 134 */         while (this.hashCodes[j] > pivot) {
/* 135 */           j--;
/*     */         }
/* 137 */         if (i <= j) {
/* 138 */           swap(i, j);
/* 139 */           i++;
/* 140 */           j--;
/*     */         }
/*     */       }
/* 143 */       if (left < j) {
/* 144 */         sort(left, j);
/*     */       }
/* 146 */       if (right > i) {
/* 147 */         sort(i, right);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void swap(int i, int j) {
/* 153 */     swap(this.hashCodes, i, j);
/* 154 */     swap(this.centralDirectoryOffsets, i, j);
/* 155 */     swap(this.positions, i, j);
/*     */   }
/*     */   
/*     */   private void swap(int[] array, int i, int j) {
/* 159 */     int temp = array[i];
/* 160 */     array[i] = array[j];
/* 161 */     array[j] = temp;
/*     */   }
/*     */   
/*     */   public Iterator<JarEntry> iterator()
/*     */   {
/* 166 */     return new EntryIterator(null);
/*     */   }
/*     */   
/*     */   public boolean containsEntry(String name) {
/* 170 */     return getEntry(name, FileHeader.class, true) != null;
/*     */   }
/*     */   
/*     */   public JarEntry getEntry(String name) {
/* 174 */     return (JarEntry)getEntry(name, JarEntry.class, true);
/*     */   }
/*     */   
/*     */   public InputStream getInputStream(String name, RandomAccessData.ResourceAccess access) throws IOException
/*     */   {
/* 179 */     FileHeader entry = getEntry(name, FileHeader.class, false);
/* 180 */     return getInputStream(entry, access);
/*     */   }
/*     */   
/*     */   public InputStream getInputStream(FileHeader entry, RandomAccessData.ResourceAccess access) throws IOException
/*     */   {
/* 185 */     if (entry == null) {
/* 186 */       return null;
/*     */     }
/* 188 */     InputStream inputStream = getEntryData(entry).getInputStream(access);
/* 189 */     if (entry.getMethod() == 8) {
/* 190 */       inputStream = new ZipInflaterInputStream(inputStream, (int)entry.getSize());
/*     */     }
/* 192 */     return inputStream;
/*     */   }
/*     */   
/*     */   public RandomAccessData getEntryData(String name) throws IOException {
/* 196 */     FileHeader entry = getEntry(name, FileHeader.class, false);
/* 197 */     if (entry == null) {
/* 198 */       return null;
/*     */     }
/* 200 */     return getEntryData(entry);
/*     */   }
/*     */   
/*     */ 
/*     */   private RandomAccessData getEntryData(FileHeader entry)
/*     */     throws IOException
/*     */   {
/* 207 */     RandomAccessData data = this.jarFile.getData();
/* 208 */     byte[] localHeader = Bytes.get(data
/* 209 */       .getSubsection(entry.getLocalHeaderOffset(), 30L));
/* 210 */     long nameLength = Bytes.littleEndianValue(localHeader, 26, 2);
/* 211 */     long extraLength = Bytes.littleEndianValue(localHeader, 28, 2);
/* 212 */     return data.getSubsection(entry.getLocalHeaderOffset() + 30L + nameLength + extraLength, entry
/* 213 */       .getCompressedSize());
/*     */   }
/*     */   
/*     */   private <T extends FileHeader> T getEntry(String name, Class<T> type, boolean cacheEntry)
/*     */   {
/* 218 */     int hashCode = AsciiBytes.hashCode(name);
/* 219 */     T entry = getEntry(hashCode, name, "", type, cacheEntry);
/* 220 */     if (entry == null) {
/* 221 */       hashCode = AsciiBytes.hashCode(hashCode, "/");
/* 222 */       entry = getEntry(hashCode, name, "/", type, cacheEntry);
/*     */     }
/* 224 */     return entry;
/*     */   }
/*     */   
/*     */   private <T extends FileHeader> T getEntry(int hashCode, String name, String suffix, Class<T> type, boolean cacheEntry)
/*     */   {
/* 229 */     int index = getFirstIndex(hashCode);
/* 230 */     while ((index >= 0) && (index < this.size) && (this.hashCodes[index] == hashCode)) {
/* 231 */       T entry = getEntry(index, type, cacheEntry);
/* 232 */       if (entry.hasName(name, suffix)) {
/* 233 */         return entry;
/*     */       }
/* 235 */       index++;
/*     */     }
/* 237 */     return null;
/*     */   }
/*     */   
/*     */   private <T extends FileHeader> T getEntry(int index, Class<T> type, boolean cacheEntry)
/*     */   {
/*     */     try
/*     */     {
/* 244 */       FileHeader cached = (FileHeader)this.entriesCache.get(Integer.valueOf(index));
/*     */       
/* 246 */       FileHeader entry = cached != null ? cached : CentralDirectoryFileHeader.fromRandomAccessData(this.centralDirectoryData, this.centralDirectoryOffsets[index], this.filter);
/*     */       
/*     */ 
/* 249 */       if ((CentralDirectoryFileHeader.class.equals(entry.getClass())) && 
/* 250 */         (type.equals(JarEntry.class))) {
/* 251 */         entry = new JarEntry(this.jarFile, (CentralDirectoryFileHeader)entry);
/*     */       }
/* 253 */       if ((cacheEntry) && (cached != entry)) {
/* 254 */         this.entriesCache.put(Integer.valueOf(index), entry);
/*     */       }
/* 256 */       return entry;
/*     */     }
/*     */     catch (IOException ex) {
/* 259 */       throw new IllegalStateException(ex);
/*     */     }
/*     */   }
/*     */   
/*     */   private int getFirstIndex(int hashCode) {
/* 264 */     int index = Arrays.binarySearch(this.hashCodes, 0, this.size, hashCode);
/* 265 */     if (index < 0) {
/* 266 */       return -1;
/*     */     }
/* 268 */     while ((index > 0) && (this.hashCodes[(index - 1)] == hashCode)) {
/* 269 */       index--;
/*     */     }
/* 271 */     return index;
/*     */   }
/*     */   
/*     */   public void clearCache() {
/* 275 */     this.entriesCache.clear();
/*     */   }
/*     */   
/*     */   private AsciiBytes applyFilter(AsciiBytes name) {
/* 279 */     return this.filter == null ? name : this.filter.apply(name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private class EntryIterator
/*     */     implements Iterator<JarEntry>
/*     */   {
/* 287 */     private int index = 0;
/*     */     
/*     */     private EntryIterator() {}
/*     */     
/* 291 */     public boolean hasNext() { return this.index < JarFileEntries.this.size; }
/*     */     
/*     */ 
/*     */     public JarEntry next()
/*     */     {
/* 296 */       if (!hasNext()) {
/* 297 */         throw new NoSuchElementException();
/*     */       }
/* 299 */       int entryIndex = JarFileEntries.this.positions[this.index];
/* 300 */       this.index += 1;
/* 301 */       return (JarEntry)JarFileEntries.this.getEntry(entryIndex, JarEntry.class, false);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\jar\JarFileEntries.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */