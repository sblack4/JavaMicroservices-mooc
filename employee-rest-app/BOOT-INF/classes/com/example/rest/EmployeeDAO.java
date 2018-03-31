package com.example.rest;

import java.util.List;

public abstract interface EmployeeDAO
{
  public abstract List<Employee> getAllEmployees();
  
  public abstract Employee getEmployee(long paramLong);
  
  public abstract List<Employee> getByLastName(String paramString);
  
  public abstract List<Employee> getByTitle(String paramString);
  
  public abstract List<Employee> getByDept(String paramString);
  
  public abstract boolean add(Employee paramEmployee);
  
  public abstract boolean update(long paramLong, Employee paramEmployee);
  
  public abstract boolean delete(long paramLong);
}


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\BOOT-INF\classes\com\example\rest\EmployeeDAO.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */