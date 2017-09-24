package com.juliuskrah.quartz.service;

import org.quartz.Scheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.juliuskrah.quartz.model.JobDescriptor;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EmailService {
	private final Scheduler scheduler;
	
	public JobDescriptor createJob(JobDescriptor descriptor) {
		return null;
	}
	
	public JobDescriptor findJob(String group, String name) {
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
