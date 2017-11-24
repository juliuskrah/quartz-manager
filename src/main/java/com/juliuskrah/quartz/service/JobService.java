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

import java.util.Optional;
import java.util.Set;

import com.juliuskrah.quartz.model.JobDescriptor;

/**
 * An object that defines the contract for defining dynamic jobs
 * 
 * @author Julius Krah
 * @since September 2017
 */
public interface JobService {

	/**
	 * Create and schedule a job by abstracting the Job and Triggers in the
	 * JobDescriptor. You must specify and name and group for this job that
	 * uniquely identifies the job. If you specify a name/group pair that is not
	 * unique, the scheduler will silently ignore the job
	 * 
	 * @param group
	 *            the group a job belongs to
	 * @param descriptor
	 *            the payload containing the Job and its associated Trigger(s).
	 *            The name and group uniquely identifies the job.
	 * @return JobDescriptor <br/>
	 *         this contains the JobDetail and Triggers of the newly created job
	 */
	JobDescriptor createJob(String group, JobDescriptor descriptor);

	/**
	 * Searches for all Jobs in the Scheduler
	 * 
	 * @return Set of JobDescriptor <br/>
	 *         this contains the JobDetail and Triggers of the newly created job
	 */
	Set<JobDescriptor> findJobs();

	/**
	 * Searches for all Jobs in the Scheduler
	 * 
	 * @param group
	 *            the group to specify
	 * 
	 * @return Set of JobDescriptor <br/>
	 *         this contains the JobDetail and Triggers of the newly created job
	 */
	Set<JobDescriptor> findGroupJobs(String group);

	/**
	 * Searches for a Job identified by the given {@code JobKey}
	 * 
	 * @param group
	 *            the group a job belongs to
	 * @param name
	 *            the name of the dynamically scheduled job
	 * @return the jobDescriptor if found or an empty Optional
	 */
	Optional<JobDescriptor> findJob(String group, String name);

	/**
	 * Updates the Job that matches the given {@code JobKey} with new
	 * information
	 * 
	 * @param group
	 *            the group a job belongs to
	 * @param name
	 *            the name of the dynamically scheduled job
	 * @param descriptor
	 *            the payload containing the updates to the JobDetail
	 */
	void updateJob(String group, String name, JobDescriptor descriptor);

	/**
	 * Deletes the Job identified by the given {@code JobKey}
	 * 
	 * @param group
	 *            the group a job belongs to
	 * @param name
	 *            the name of the dynamically scheduled job
	 */
	void deleteJob(String group, String name);

	/**
	 * Deletes all jobs stored in the {@code Scheduler}
	 */
	void deleteAllJobs();

	/**
	 * Pauses the Job identified by the given {@code JobKey}
	 * 
	 * @param group
	 *            the group a job belongs to
	 * @param name
	 *            the name of the dynamically scheduled job
	 */
	void pauseJob(String group, String name);

	/**
	 * Resumes the Job identified by the given {@code JobKey}
	 * 
	 * @param group
	 *            the group a job belongs to
	 * @param name
	 *            the name of the dynamically scheduled job
	 */
	void resumeJob(String group, String name);
}
