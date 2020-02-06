# Quartz Manager (Spring WebFlux Functional)

## Branches

Branch                                                              | Notes
--------------------------------------------------------------------|------------------
[master](https://github.com/juliuskrah/quartz-manager/tree/master)  | Implementation without database
[v1.x](https://github.com/juliuskrah/quartz-manager/tree/v1.x)      | Implementation with database
[v2.x](https://github.com/juliuskrah/quartz-manager/tree/v2.x)      | Implementation with error handling
[v3.x](https://github.com/juliuskrah/quartz-manager/tree/v3.x)      | Implementation with Spring WebFlux
[v4.x](https://github.com/juliuskrah/quartz-manager/tree/v4.x)      | Implementation with Functional Spring WebFlux

## Handler Functions

This branch rewrites the entire Spring with Quartz project using Spring 5's
WebFlux.  
In this branch I use the `Handler Functions` for functional reactive

```java
@Component
public class EmailHandler {

	public Mono<ServerResponse> createJob(ServerRequest request) {
		Mono<JobDescriptor> descriptor = request.bodyToMono(JobDescriptor.class);
		String group = request.pathVariable("group");
		return descriptor.flatMap(d -> {
			jobService.createJob(group, d);
			URI uri = request.uriBuilder().path("/{job}").build(d.getName());
			return created(uri).build();
		});
	}

	public Mono<ServerResponse> findGroupJobs(ServerRequest request) {
		String group = request.pathVariable("group");
		Flux<JobDescriptor> jobs = jobService.findGroupJobs(group);
		return ok().body(fromPublisher(jobs, JobDescriptor.class));
	}
	
	// ...
}
```

To understand what is happening in this project read the blog post at:

<http://juliuskrah.com/tutorial/2017/10/11/error-handling-in-a-rest-service-with-quartz/>

for a comprehensive overview.

## Quick Start

*Prerequisite:* JDK 10

Clone this repository

```bash
> git clone -b v3.x https://github.com/juliuskrah/quartz-manager.git
> cd quartz-manager
> mvnw clean spring-boot:run
```

Using docker

```bash
> docker run -p 8080:8080 juliuskrah/quartz-manager:4.x
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
         "cron": "0 0 0 ? JAN MON 2020"
       }
    ]
}
```

Content-Type: `application/json`

**VIEW**  
Method      : `GET: /api/v1.0/groups/:group/jobs/:name`  
Status      : `200: Ok`  or `404: Not Found`  
Body        : NULL  
Accept      : `application/json`

**VIEW (By Group)**  
Method      : `GET: /api/v1.0/groups/:group/jobs/`  
Status      : `200: Ok`  
Body        : NULL  
Accept      : `application/json`

**VIEW (All)**  
Method      : `GET: /api/v1.0/jobs/`  
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
