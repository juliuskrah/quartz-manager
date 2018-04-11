package com.juliuskrah.quartz.service;

import static org.quartz.JobKey.jobKey;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
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
			log.error("Could not save job with key - {} due to error - {}", jobDetail.getKey(), e.getLocalizedMessage());
			throw new IllegalArgumentException(e.getLocalizedMessage());
		}
		return descriptor;
	}
	
	@Transactional(readOnly = true)
	public Optional<JobDescriptor> findJob(String group, String name) {
		// @formatter:off
		try {
			JobDetail jobDetail = scheduler.getJobDetail(jobKey(name, group));
			if(Objects.nonNull(jobDetail))
				return Optional.of(
						JobDescriptor.buildDescriptor(jobDetail, 
								scheduler.getTriggersOfJob(jobKey(name, group))));
		} catch (SchedulerException e) {
			log.error("Could not find job with key - {}.{} due to error - {}", group, name, e.getLocalizedMessage());
		}
		// @formatter:on
		log.warn("Could not find job with key - {}.{}", group, name);
		return Optional.empty();
	}
	
	public void updateJob(String group, String name, JobDescriptor descriptor) {
		try {
			JobDetail oldJobDetail = scheduler.getJobDetail(jobKey(name, group));
			if(Objects.nonNull(oldJobDetail)) {
				JobDataMap jobDataMap = oldJobDetail.getJobDataMap();
				jobDataMap.put("subject", descriptor.getSubject());
				jobDataMap.put("messageBody", descriptor.getMessageBody());
				jobDataMap.put("to", descriptor.getTo());
				jobDataMap.put("cc", descriptor.getCc());
				jobDataMap.put("bcc", descriptor.getBcc());
				JobBuilder jb = oldJobDetail.getJobBuilder();
				JobDetail newJobDetail = jb.usingJobData(jobDataMap).storeDurably().build();
				Set<Trigger> triggersForJob = descriptor.buildTriggers();
				List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey(name, group));
				for(Trigger trigger : triggers) {
					scheduler.unscheduleJob(trigger.getKey());
				}
				scheduler.scheduleJob(newJobDetail, triggersForJob, true);
				log.info("Updated job with key - {}", newJobDetail.getKey());
				return;
			}
			log.warn("Could not find job with key - {}.{} to update", group, name);
		} catch (SchedulerException e) {
			log.error("Could not find job with key - {}.{} to update due to error - {}", group, name, e.getLocalizedMessage());
		}
	}
	
	public void deleteJob(String group, String name) {
		try {
			scheduler.deleteJob(jobKey(name, group));
			log.info("Deleted job with key - {}.{}", group, name);
		} catch (SchedulerException e) {
			log.error("Could not delete job with key - {}.{} due to error - {}", group, name, e.getLocalizedMessage());
		}
	}
	
	public void pauseJob(String group, String name) {
		try {
			scheduler.pauseJob(jobKey(name, group));
			log.info("Paused job with key - {}.{}", group, name);
		} catch (SchedulerException e) {
			log.error("Could not pause job with key - {}.{} due to error - {}", group, name, e.getLocalizedMessage());
		}
	}
	
	public void resumeJob(String group, String name) {
		try {
			scheduler.resumeJob(jobKey(name, group));
			log.info("Resumed job with key - {}.{}", group, name);
		} catch (SchedulerException e) {
			log.error("Could not resume job with key - {}.{} due to error - {}", group, name, e.getLocalizedMessage());
		}
	}
}
