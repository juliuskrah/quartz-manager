# Quartz Manager
Dynanic Job Scheduling with Quartz and Spring.
To understand what is happening in this project read the blog post at:

<http://juliuskrah.com/tutorial/2017/10/06/persisting-dynamic-jobs-with-quartz-and-spring/>

for a comprehensive overview.

## Branches

Branch                                                              | Notes
--------------------------------------------------------------------|------------------
[master](https://github.com/juliuskrah/quartz-manager/tree/master)  | Implementation without database      
[v1.x](https://github.com/juliuskrah/quartz-manager/tree/v1.x)      | Implementation with database
[v2.x](https://github.com/juliuskrah/quartz-manager/tree/v2.x)      | Implementation with error handling
[v3.x](https://github.com/juliuskrah/quartz-manager/tree/v3.x)      | Implementation with Spring WebFlux
[v4.x ](https://github.com/juliuskrah/quartz-manager/tree/v4.x)     | Implementation with Functional Spring WebFlux functional 

## Quick Start

After executing the following command, the application will start on `localhost:8080`

- Using maven:

```bash
> mvnw clean spring-boot:run
```

- Using docker:

```bash
> docker run -p 8080:8080 juliuskrah/quartz-manager
```

## Features
**CREATE**  
Method      : `POST: /api/v1.0/groups/:group/jobs`  
Status      : `201: Created`  
Body        :
```json
{
  "name": "manager",
  "subject": "Daily Fuel Report",
  "messageBody": "Sample fuel report",
  "to": ["juliuskrah@example.com", "juliuskrah@example.net"],
  "triggers":
    [
       {
         "name": "manager",
         "group": "email",
         "fireTime": "2017-10-02T22:00:00.000"
       }
    ]
}
```
Content-Type: `application/json`

**VIEW**  
Method      : `GET: /api/v1.0/groups/:group/jobs/:name`  
Status      : `200: Ok`  
Body        : NULL  
Accept      : `application/json`

**UPDATE**  
Method      : `PUT: /api/v1.0/groups/:group/jobs/:name`  
Status      : `204: No Content`  
Body        :
```json
{
  "name": "manager",
  "subject": "Daily Fuel Report",
  "messageBody": "Sample fuel report",
  "to" : ["juliuskrah@example.com", "juliuskrah@example.net"],
  "cc" : ["management@example.com", "management@example.net"],
  "bcc": ["bcc@example.com"]
}
```
Content-Type: `application/json`

**UPDATE (Pause)**  
Method      : `PATCH: /api/v1.0/groups/:group/jobs/:name/pause`  
Status      : `204: No Content`  
Body        : NULL  
Content-Type: `*/*`

**UPDATE (Resume)**  
Method      : `PATCH: /api/v1.0/groups/:group/jobs/:name/resume`  
Status      : `204: No Content`  
Body        : NULL  
Content-Type: `*/*`

**DELETE**  
Method      : `DELETE: /api/v1.0/groups/:group/jobs/:name`  
Status      : `204: No Content`  
Body        : NULL  
Content-Type: `*/*`
