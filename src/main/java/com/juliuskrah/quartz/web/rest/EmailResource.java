/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.juliuskrah.quartz.web.rest;

import com.juliuskrah.quartz.model.JobDescriptor;
import com.juliuskrah.quartz.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/v1.0")
@RequiredArgsConstructor
public class EmailResource {
	private final JobService jobService;

	/**
	 * POST /api/v1.0/groups/:group/jobs
	 *
	 * @param group
	 * @param descriptor
	 * @return
	 */
	@PostMapping(path = "/groups/{group}/jobs")
	public ResponseEntity<Void> createJob(
	        @PathVariable String group, @Valid @RequestBody JobDescriptor descriptor, UriComponentsBuilder builder) {
        URI location = builder.path("/{job}").buildAndExpand(descriptor.getName()).toUri();
        jobService.createJob(group, descriptor);
		return ResponseEntity.created(location).build();
	}

	/**
	 * GET /api/v1.0/groups/:group/jobs
	 *
	 * @param group
	 * @return
	 */
	@GetMapping(path = "/groups/{group}/jobs")
	public Flux<JobDescriptor>  findGroupJobs(@PathVariable String group) {
		return jobService.findGroupJobs(group);
	}

	/**
	 * GET /api/v1.0/jobs
	 *
	 * @return
	 */
	@GetMapping(path = "/jobs")
	public Flux<JobDescriptor> findJobs() {
		return jobService.findJobs();
	}

	/**
	 * GET /api/v1.0/groups/:group/jobs/:name
	 *
	 * @param group
	 * @param name
	 * @return
	 */
	@GetMapping(path = "/groups/{group}/jobs/{name}")
	public Mono<ResponseEntity<JobDescriptor>> findJob(@PathVariable String group, @PathVariable String name) {
		return jobService.findJob(group, name)
                .map(ResponseEntity::ok)    // If job is found return 200
                .defaultIfEmpty(ResponseEntity.notFound().build()); // Return 404 if job is not found
	}

	/**
	 * PUT /api/v1.0/groups/:group/jobs/:name
	 *
	 * @param group
	 * @param name
	 * @param descriptor
	 * @return
	 */
    @ResponseStatus(NO_CONTENT)
	@PutMapping(path = "/groups/{group}/jobs/{name}")
	public void updateJob(@PathVariable String group, @PathVariable String name, @Valid @RequestBody JobDescriptor descriptor) {
		jobService.updateJob(group, name, descriptor);
	}

	/**
	 * DELETE /api/v1.0/groups/:group/jobs/:name
	 *
	 * @param group
	 * @param name
	 * @return
	 */
    @ResponseStatus(NO_CONTENT)
	@DeleteMapping(path = "/groups/{group}/jobs/{name}")
	public void deleteJob(@PathVariable String group, @PathVariable String name) {
		jobService.deleteJob(group, name);
	}

	/**
	 * PATCH /api/v1.0/groups/:group/jobs/:name/pause
	 *
	 * @param group
	 * @param name
	 * @return
	 */
    @ResponseStatus(NO_CONTENT)
	@PatchMapping(path = "/groups/{group}/jobs/{name}/pause")
	public void pauseJob(@PathVariable String group, @PathVariable String name) {
		jobService.pauseJob(group, name);
	}

	/**
	 * PATCH /api/v1.0/groups/:group/jobs/:name/resume
	 *
	 * @param group
	 * @param name
	 * @return
	 */
    @ResponseStatus(NO_CONTENT)
	@PatchMapping(path = "/groups/{group}/jobs/{name}/resume")
	public void resumeJob(@PathVariable String group, @PathVariable String name) {
		jobService.resumeJob(group, name);
	}
}
