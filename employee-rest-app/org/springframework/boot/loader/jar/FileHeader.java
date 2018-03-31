package org.springframework.boot.loader.jar;

abstract interface FileHeader
{
  public abstract boolean hasName(String paramString1, String paramString2);
  
  public abstract long getLocalHeaderOffset();
  
  public abstract long getCompressedSize();
  
  public abstract long getSize();
  
  public abstract int getMethod();
}


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\jar\FileHeader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */