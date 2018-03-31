/*     */ package com.example.rest;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.springframework.http.HttpStatus;
/*     */ import org.springframework.http.ResponseEntity;
/*     */ import org.springframework.web.bind.annotation.CrossOrigin;
/*     */ import org.springframework.web.bind.annotation.PathVariable;
/*     */ import org.springframework.web.bind.annotation.RequestBody;
/*     */ import org.springframework.web.bind.annotation.RequestMapping;
/*     */ import org.springframework.web.bind.annotation.RestController;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @CrossOrigin
/*     */ @RestController
/*     */ @RequestMapping({"/employees"})
/*     */ public class EmployeeController
/*     */ {
/*  20 */   EmployeeDAO edao = new EmployeeListDAO();
/*     */   
/*     */   @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET})
/*     */   public Employee[] getAll()
/*     */   {
/*  25 */     return (Employee[])this.edao.getAllEmployees().toArray(new Employee[0]);
/*     */   }
/*     */   
/*     */ 
/*     */   @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, value={"{id}"})
/*     */   public ResponseEntity get(@PathVariable long id)
/*     */   {
/*  32 */     Employee match = null;
/*  33 */     match = this.edao.getEmployee(id);
/*     */     
/*  35 */     if (match != null) {
/*  36 */       return new ResponseEntity(match, HttpStatus.OK);
/*     */     }
/*  38 */     return new ResponseEntity(null, HttpStatus.NOT_FOUND);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, value={"/lastname/{name}"})
/*     */   public ResponseEntity getByLastName(@PathVariable String name)
/*     */   {
/*  46 */     List<Employee> matchList = this.edao.getByLastName(name);
/*     */     
/*  48 */     if (matchList.size() > 0) {
/*  49 */       return new ResponseEntity(matchList.toArray(new Employee[0]), HttpStatus.OK);
/*     */     }
/*  51 */     return new ResponseEntity(null, HttpStatus.NOT_FOUND);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, value={"/title/{name}"})
/*     */   public ResponseEntity getByTitle(@PathVariable String name)
/*     */   {
/*  60 */     List<Employee> matchList = this.edao.getByTitle(name);
/*     */     
/*  62 */     if (matchList.size() > 0) {
/*  63 */       return new ResponseEntity(matchList.toArray(new Employee[0]), HttpStatus.OK);
/*     */     }
/*  65 */     return new ResponseEntity(null, HttpStatus.NOT_FOUND);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, value={"/department/{name}"})
/*     */   public ResponseEntity getByDept(@PathVariable String name)
/*     */   {
/*  73 */     List<Employee> matchList = this.edao.getByDept(name);
/*     */     
/*  75 */     if (matchList.size() > 0) {
/*  76 */       return new ResponseEntity(matchList.toArray(new Employee[0]), HttpStatus.OK);
/*     */     }
/*  78 */     return new ResponseEntity(null, HttpStatus.NOT_FOUND);
/*     */   }
/*     */   
/*     */ 
/*     */   @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
/*     */   public ResponseEntity add(@RequestBody Employee employee)
/*     */   {
/*  85 */     if (this.edao.add(employee)) {
/*  86 */       return new ResponseEntity(null, HttpStatus.CREATED);
/*     */     }
/*  88 */     return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.PUT}, value={"{id}"})
/*     */   public ResponseEntity update(@PathVariable long id, @RequestBody Employee employee)
/*     */   {
/*  96 */     if (this.edao.update(id, employee)) {
/*  97 */       return new ResponseEntity(null, HttpStatus.OK);
/*     */     }
/*  99 */     return new ResponseEntity(null, HttpStatus.NOT_FOUND);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.DELETE}, value={"{id}"})
/*     */   public ResponseEntity delete(@PathVariable long id)
/*     */   {
/* 107 */     boolean result = this.edao.delete(id);
/*     */     
/* 109 */     if (result) {
/* 110 */       return new ResponseEntity(null, HttpStatus.OK);
/*     */     }
/* 112 */     return new ResponseEntity(null, HttpStatus.NOT_FOUND);
/*     */   }
/*     */ }


/* Location:              C:\Users\gense\Git\JavaMicroservices-mooc\lab01-local-app\EmployeeRESTApp-1.0.jar!\BOOT-INF\classes\com\example\rest\EmployeeController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */