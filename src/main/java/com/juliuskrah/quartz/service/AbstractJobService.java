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
import static org.quartz.impl.matchers.GroupMatcher.anyJobGroup;
import static org.quartz.impl.matchers.GroupMatcher.jobGroupEquals;

import java.util.HashSet;
import static java.util.Objects.*;
import java.util.Optional;
import java.util.Set;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.transaction.annotation.Transactional;

import com.juliuskrah.quartz.model.JobDescriptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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

	private Mono<JobDescriptor> descriptor(JobDetail detail) {
	    try {
            return Mono.just(
                    JobDescriptor.buildDescriptor(detail,
                    scheduler.getTriggersOfJob(detail.getKey()))
            );
        } catch (SchedulerException e) {
            log.error("Could not find job with key - {} due to error - {}", detail.getKey(), e.getLocalizedMessage());
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract JobDescriptor createJob(String group, JobDescriptor descriptor);
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<JobDescriptor> findJobs(){
		Set<JobDescriptor> descriptors = new HashSet<>();
		 try {
	            Set<JobKey> keys = scheduler.getJobKeys(anyJobGroup());
	            for(JobKey key : keys) {
	            	JobDetail jobDetail = scheduler.getJobDetail(key);
	            	descriptors.add(
	            			JobDescriptor.buildDescriptor(jobDetail, 
	            					scheduler.getTriggersOfJob(key)));
	            }
	        } catch (SchedulerException e) {
	        	log.error("Could not find any jobs due to error - {}", e.getLocalizedMessage(), e);
				throw new RuntimeException(e.getLocalizedMessage());
	        }
		return descriptors;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<JobDescriptor> findGroupJobs(String group){
		Set<JobDescriptor> descriptors = new HashSet<>();
		 try {
	            Set<JobKey> keys = scheduler.getJobKeys(jobGroupEquals(group));
	            for(JobKey key : keys) {
	            	JobDetail jobDetail = scheduler.getJobDetail(key);
	            	descriptors.add(
	            			JobDescriptor.buildDescriptor(jobDetail, 
	            					scheduler.getTriggersOfJob(key)));
	            }
	        } catch (SchedulerException e) {
	        	log.error("Could not find any jobs due to error - {}", e.getLocalizedMessage(), e);
				throw new RuntimeException(e.getLocalizedMessage());
	        }
		return descriptors;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = true)
	@Override
	public Mono<JobDescriptor> findJob(String group, String name) {
		// @formatter:off
		try {
			JobDetail jobDetail = scheduler.getJobDetail(jobKey(name, group));
            if(nonNull(jobDetail))
                return Mono.just(jobDetail)
                    .flatMap(this::descriptor);
			log.warn("Could not find job with key - {}.{}", group, name);
		} catch (SchedulerException e) {
			log.error("Could not find job with key - {}.{} due to error", group, name, e);
			throw new RuntimeException(e.getLocalizedMessage());
		}
		// @formatter:on
        return Mono.empty();
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
			throw new RuntimeException(e.getLocalizedMessage());
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
			throw new RuntimeException(e.getLocalizedMessage());
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
			throw new RuntimeException(e.getLocalizedMessage());
		}
	}
}
