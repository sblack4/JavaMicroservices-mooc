#!/bin/bash
echo Executing: curl -X POST -i -H "Content-Type: application/json" http://localhost:8080/employees
curl -X POST -i -H "Content-Type: application/json" -d '{"id":140,"firstName":"Reed","lastName":"Hernal","email":"Reed.Hernal@example.com","phone":"429-071-2018","birthDate":"1977-02-05T19:18:57.407Z","title":"Future Directives Facilitator","department":"Mobility"}' http://localhost:8080/employees
