package com.juliuskrah.quartz.web.rest;

import static org.springframework.http.HttpStatus.*;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juliuskrah.quartz.model.JobDescriptor;
import com.juliuskrah.quartz.service.EmailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1.0")
@RequiredArgsConstructor
public class EmailResource {
	private final EmailService emailService;

	@PostMapping(path = "/groups/{group}/jobs")
	public ResponseEntity<JobDescriptor> createJob(@PathVariable String group, @Valid @RequestBody JobDescriptor descriptor) {
		return new ResponseEntity<>(emailService.createJob(group, descriptor), CREATED);
	}

	@GetMapping(path = "/groups/{group}/jobs/{name}")
	public ResponseEntity<JobDescriptor> findJob(@PathVariable String group, @PathVariable String name) {
		return null;
	}

	@PutMapping(path = "/groups/{group}/jobs/{name}")
	public ResponseEntity<Void> updateJob(@PathVariable String group, @PathVariable String name, @RequestBody JobDescriptor descriptor) {
		return null;
	}

	@DeleteMapping(path = "/groups/{group}/jobs/{name}")
	public ResponseEntity<JobDescriptor> deleteJob(@PathVariable String group, @PathVariable String name) {
		return null;
	}

	@PatchMapping(path = "/groups/{group}/jobs/{name}/pause")
	public ResponseEntity<JobDescriptor> pauseJob(@PathVariable String group, @PathVariable String name) {
		return null;
	}

	@PatchMapping(path = "/groups/{group}/jobs/{name}/resume")
	public ResponseEntity<JobDescriptor> resumeJob(@PathVariable String group, @PathVariable String name) {
		return null;
	}
}
