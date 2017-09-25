package com.juliuskrah.quartz.service;

import java.util.Optional;
import java.util.Set;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.juliuskrah.quartz.model.JobDescriptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmailService {
	private final Scheduler scheduler;
	
	public JobDescriptor createJob(String group, JobDescriptor descriptor) {
		descriptor.setGroup(group);
		JobDetail jobDetail = descriptor.buildJobDetail();
		Set<Trigger> triggersForJob = descriptor.buildTriggers();
		log.info("About to save job with key - {}", jobDetail.getKey());
		try {
			scheduler.scheduleJob(jobDetail, triggersForJob, false);
			log.info("Job with key - {} saved sucessfully", jobDetail.getKey());
		} catch (SchedulerException e) {
			log.error("Could not save job with key - {}", jobDetail.getKey());
			throw new IllegalArgumentException(e.getLocalizedMessage());
		}
		return descriptor;
	}
	
	@Transactional(readOnly = true)
	public Optional<JobDescriptor> findJob(String group, String name) {
		return null;
	}
	
	public void updateJob(JobDescriptor descriptor) {
		return;
	}
	
	public void deleteJob(String group, String name) {
		return;
	}
	
	public void pauseJob(String group, String name) {
		return;
	}
	
	public void resumeJob(String group, String name) {
		return;
	}
}
