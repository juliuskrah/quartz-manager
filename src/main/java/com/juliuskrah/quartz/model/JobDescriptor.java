package com.juliuskrah.quartz.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import static org.quartz.JobBuilder.*;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.juliuskrah.quartz.jobs.EmailJob;

import lombok.Data;

@Data
public class JobDescriptor {
	// TODO add boolean fields for HTML and Attachments
	@NotBlank
	private String name;
	private String group;
	@NotEmpty
	private String subject;
	@NotEmpty
	private String messageBody;
	@NotEmpty
	private List<String> to;
	private List<String> cc;
	private List<String> bcc;
	private Map<String, Object> data = new LinkedHashMap<>();
	@JsonProperty("triggers")
	private List<TriggerDescriptor> triggerDescriptors = new ArrayList<>();

	public JobDescriptor setName(final String name) {
		this.name = name;
		return this;
	}

	public JobDescriptor setGroup(final String group) {
		this.group = group;
		return this;
	}

	public JobDescriptor setSubject(String subject) {
		this.subject = subject;
		return this;
	}
	
	public JobDescriptor setMessageBody(String messageBody) {
		this.messageBody = messageBody;
		return this;
	}
	
	public JobDescriptor setTo(List<String> to) {
		this.to = to;
		return this;
	}
	
	public JobDescriptor setCc(List<String> cc) {
		this.cc = cc;
		return this;
	}
	
	public JobDescriptor setBcc(List<String> bcc) {
		this.bcc = bcc;
		return this;
	}
 	
	public JobDescriptor setData(final Map<String, Object> data) {
		this.data = data;
		return this;
	}

	public JobDescriptor setTriggerDescriptors(final List<TriggerDescriptor> triggerDescriptors) {
		this.triggerDescriptors = triggerDescriptors;
		return this;
	}

	@JsonIgnore
	public Set<Trigger> buildTriggers() {
		Set<Trigger> triggers = new LinkedHashSet<>();
		for (TriggerDescriptor triggerDescriptor : triggerDescriptors) {
			triggers.add(triggerDescriptor.buildTrigger());
		}

		return triggers;
	}

	public JobDetail buildJobDetail() {
		// @formatter:off
		JobDataMap jobDataMap = new JobDataMap(getData());
		jobDataMap.put("subject", subject);
		jobDataMap.put("messageBody", messageBody);
		jobDataMap.put("to", to);
		jobDataMap.put("cc", cc);
		jobDataMap.put("bcc", bcc);
		return newJob(EmailJob.class)
                .withIdentity(getName(), getGroup())
                .usingJobData(jobDataMap)
                .build();
		// @formatter:on
	}
	
	@SuppressWarnings("unchecked")
	public JobDescriptor buildDescriptor(JobDetail jobDetail, List<? extends Trigger> triggersOfJob) {
		// @formatter:off
		List<TriggerDescriptor> triggerDescriptors = new ArrayList<>();

		for (Trigger trigger : triggersOfJob) {
		    triggerDescriptors.add(TriggerDescriptor.buildDescriptor(trigger));
		}
		
		return new JobDescriptor()
				.setName(jobDetail.getKey().getName())
				.setGroup(jobDetail.getKey().getGroup())
				.setSubject(jobDetail.getJobDataMap().getString("subject"))
				.setMessageBody(jobDetail.getJobDataMap().getString("messageBody"))
				.setTo((List<String>)jobDetail.getJobDataMap().get("to"))
				.setCc((List<String>)jobDetail.getJobDataMap().get("cc"))
				.setBcc((List<String>)jobDetail.getJobDataMap().get("bcc"))
				.setData(jobDetail.getJobDataMap().getWrappedMap())
				.setTriggerDescriptors(triggerDescriptors);
		// @formatter:on
    }
}
