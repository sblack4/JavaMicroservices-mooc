package org.springframework.boot.loader.jar;

import org.springframework.boot.loader.data.RandomAccessData;

abstract interface CentralDirectoryVisitor
{
  public abstract void visitStart(CentralDirectoryEndRecord paramCentralDirectoryEndRecord, RandomAccessData paramRandomAccessData);
  
  public abstract void visitFileHeader(CentralDirectoryFileHeader paramCentralDirectoryFileHeader, int paramInt);
  
  public abstract void visitEnd();
}


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\jar\CentralDirectoryVisitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */