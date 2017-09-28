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
import java.util.Optional;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.transaction.annotation.Transactional;

import com.juliuskrah.quartz.model.JobDescriptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract implementation of JobService
 * 
 * @author Julius Krah
 * @since September 2017
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractJobService implements JobService {
	protected final Scheduler scheduler;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract JobDescriptor createJob(String group, JobDescriptor descriptor);

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = true)
	@Override
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void updateJob(String group, String name, JobDescriptor descriptor);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteJob(String group, String name) {
		try {
			scheduler.deleteJob(jobKey(name, group));
			log.info("Deleted job with key - {}.{}", group, name);
		} catch (SchedulerException e) {
			log.error("Could not delete job with key - {}.{} due to error - {}", group, name, e.getLocalizedMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pauseJob(String group, String name) {
		try {
			scheduler.pauseJob(jobKey(name, group));
			log.info("Paused job with key - {}.{}", group, name);
		} catch (SchedulerException e) {
			log.error("Could not pause job with key - {}.{} due to error - {}", group, name, e.getLocalizedMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resumeJob(String group, String name) {
		try {
			scheduler.resumeJob(jobKey(name, group));
			log.info("Resumed job with key - {}.{}", group, name);
		} catch (SchedulerException e) {
			log.error("Could not resume job with key - {}.{} due to error - {}", group, name, e.getLocalizedMessage());
		}
	}
}
