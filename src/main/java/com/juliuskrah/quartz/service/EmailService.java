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
package com.juliuskrah.quartz.service;

import static org.quartz.JobKey.jobKey;

import java.util.Objects;
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

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@code JobService} that schedules email dynamically
 * 
 * @author Julius Krah
 * @since September 2017
 */
@Slf4j
@Service
@Transactional
public class EmailService extends AbstractJobService {

	public EmailService(Scheduler scheduler) {
		super(scheduler);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateJob(String group, String name, JobDescriptor descriptor) {
		try {
			JobDetail oldJobDetail = scheduler.getJobDetail(jobKey(name, group));
			if (Objects.nonNull(oldJobDetail)) {
				JobDataMap jobDataMap = oldJobDetail.getJobDataMap();
				jobDataMap.put("subject", descriptor.getSubject());
				jobDataMap.put("messageBody", descriptor.getMessageBody());
				jobDataMap.put("to", descriptor.getTo());
				jobDataMap.put("cc", descriptor.getCc());
				jobDataMap.put("bcc", descriptor.getBcc());
				JobBuilder jb = oldJobDetail.getJobBuilder();
				JobDetail newJobDetail = jb.usingJobData(jobDataMap).storeDurably().build();
				scheduler.addJob(newJobDetail, true);
				log.info("Updated job with key - {}", newJobDetail.getKey());
				return;
			}
			log.warn("Could not find job with key - {}.{} to update", group, name);
		} catch (SchedulerException e) {
			log.error("Could not find job with key - {}.{} to update due to error - {}", group, name, e.getLocalizedMessage());
		}
	}

}
