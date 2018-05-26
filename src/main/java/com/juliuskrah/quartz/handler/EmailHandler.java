package com.juliuskrah.quartz.handler;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import static org.springframework.web.reactive.function.server.ServerResponse.created;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import java.net.URI;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.juliuskrah.quartz.model.JobDescriptor;
import com.juliuskrah.quartz.service.JobService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class EmailHandler {
	private final JobService jobService;

	/**
	 * POST /api/v1.0/groups/:group/jobs
	 * Path: group Body: JobDescriptor
	 */
	public Mono<ServerResponse> createJob(ServerRequest request) {
		Mono<JobDescriptor> descriptor = request.bodyToMono(JobDescriptor.class);
		String group = request.pathVariable("group");
		return descriptor.flatMap(d -> {
			jobService.createJob(group, d);
			URI uri = request.uriBuilder().path("/{job}").build(d.getName());
			return created(uri).build();
		});
	}

	/**
	 * GET /api/v1.0/groups/:group/jobs
	 * Path: group
	 */
	public Mono<ServerResponse> findGroupJobs(ServerRequest request) {
		String group = request.pathVariable("group");
		Flux<JobDescriptor> jobs = jobService.findGroupJobs(group);
		return ok().body(fromPublisher(jobs, JobDescriptor.class));
	}
	
	/**
	 * GET /api/v1.0/jobs
	 */
	public Mono<ServerResponse> findJobs(ServerRequest request) {
		Flux<JobDescriptor> jobs = jobService.findJobs();
		return ok().body(fromPublisher(jobs, JobDescriptor.class));
	}
	
	/**
	 * GET /api/v1.0/groups/:group/jobs/:name
	 * Path: group, name
	 */
	public Mono<ServerResponse> findJob(ServerRequest request) {
		String group = request.pathVariable("group");
		String name = request.pathVariable("name");
		Mono<ServerResponse> notFound = notFound().build();
		Mono<JobDescriptor> job = jobService.findJob(group, name);
		
		return job
				.flatMap(j -> ok().body(fromObject(j)))
				.switchIfEmpty(notFound);

	}
	
	/**
	 * PUT /api/v1.0/groups/:group/jobs/:name
	 * Path: group, name
	 */
	public Mono<ServerResponse> updateJob(ServerRequest request) {
		Mono<JobDescriptor> descriptor = request.bodyToMono(JobDescriptor.class);
		String group = request.pathVariable("group");
		String name = request.pathVariable("name");
		return descriptor.flatMap(d -> {
			jobService.updateJob(group, name, d);
			return ServerResponse.noContent().build();
		});
	}
	
	/**
	 * DELETE /api/v1.0/groups/:group/jobs/:name
	 * Path: group, name
	 */
	public Mono<ServerResponse> deleteJob(ServerRequest request) {
		String group = request.pathVariable("group");
		String name = request.pathVariable("name");
		jobService.deleteJob(group, name);
		return ServerResponse.noContent().build();
	}
	

	/**
	 * PATCH /api/v1.0/groups/:group/jobs/:name/pause
	 * Path: group, name
	 */
	public Mono<ServerResponse> pauseJob(ServerRequest request) {
		String group = request.pathVariable("group");
		String name = request.pathVariable("name");
		jobService.pauseJob(group, name);
		return ServerResponse.noContent().build();
	}
	

	/**
	 * PATCH /api/v1.0/groups/:group/jobs/:name/resume
	 * Path: group, name
	 */
	public Mono<ServerResponse> resumeJob(ServerRequest request) {
		String group = request.pathVariable("group");
		String name = request.pathVariable("name");
		jobService.resumeJob(group, name);
		return ServerResponse.noContent().build();
	}
}
