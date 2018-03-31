package org.springframework.boot.loader.archive;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.jar.Manifest;

public abstract interface Archive
  extends Iterable<Entry>
{
  public abstract URL getUrl()
    throws MalformedURLException;
  
  public abstract Manifest getManifest()
    throws IOException;
  
  public abstract List<Archive> getNestedArchives(EntryFilter paramEntryFilter)
    throws IOException;
  
  public static abstract interface EntryFilter
  {
    public abstract boolean matches(Archive.Entry paramEntry);
  }
  
  public static abstract interface Entry
  {
    public abstract boolean isDirectory();
    
    public abstract String getName();
  }
}


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\org\springframework\boot\loader\archive\Archive.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */