# Java-MicroServices mooc
from Oracle Learning Library

## About 
My notes while taking the mooc 
[Oracle MOOC: Managing Java Cloud Services Using REST	
](https://apexapps.oracle.com/pls/apex/f?p=44785:149:0::::P17_EVENT_ID:5746)

### description - from the website 
Description
During this course you will learn the fundamentals of managing Oracle Java Cloud Services using REST, including:

* Basic REST Concepts(review)
* Oracle Java Cloud Service Concepts and REST interfaces
* Stack Manager Principles as they apply to Java Cloud Service and REST
* Provisioning Java Cloud Service instances using Stack Manager
* How to start/stop/manage and monitor Java Cloud Services using REST
* How to backup, patch, scale in/out and up/down Java Cloud Service instances using REST
* Working with Coherence and Traffic Director using REST.
* During this free, 3-week MOOC you will view topic based videos, complete homework and interact with fellow students and facilitators to guide your experience in gaining an understanding of Oracle's Java Cloud Service and how it can be provisioned and managed using REST

### sessions taken 
* 04 April 2018 - Week 1
* 11 April 2018 - Week 2
* 18 April 2018 - Week 3

## running accs-like environments in docker
https://blogs.oracle.com/weblogicserver/oracle-weblogic-server-1221-running-on-docker-containers  

## lab01 curl - code an employees api in java 
See [lab01.pdf](lab01.pdf) for instructions 

## lab01 accs app - deploy Employee REST App to accs
we'll use docker because I don't have an instance  
See [lab01.pdf](lab01.pdf) for instructions 
to make an trial account, make an instance, & deploy 
to accs :smiley:

```shell
# TODO:(sblack4) command to run in docker
#  docker-compose up or something...
```

## lab01 local app - jar to run locally
from this directory 
```shell
cd lab01-local-app

java -jar EmployeeRESTApp-1.0.jar
```