package com.juliuskrah.quartz.model;

import static java.time.ZoneId.systemDefault;
import static java.util.UUID.randomUUID;
import static org.quartz.CronExpression.isValidExpression;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.springframework.util.StringUtils.isEmpty;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.TimeZone;

import org.quartz.JobDataMap;
import org.quartz.Trigger;

import lombok.Data;

@Data
public class TriggerDescriptor {
	private String name;
	private String group;
	private LocalDateTime fireTime;
	private String cron;

	public TriggerDescriptor setName(final String name) {
		this.name = name;
		return this;
	}

	public TriggerDescriptor setGroup(final String group) {
		this.group = group;
		return this;
	}

	public TriggerDescriptor setFireTime(final LocalDateTime fireTime) {
		this.fireTime = fireTime;
		return this;
	}

	public TriggerDescriptor setCron(final String cron) {
		this.cron = cron;
		return this;
	}

	private String buildName() {
		return isEmpty(name) ? randomUUID().toString() : name;
	}

	public Trigger buildTrigger() {
		if (!isValidExpression(cron))
			throw new IllegalArgumentException("Provided expression " + cron + " is not a valid cron expression");
		// @formatter:off
        if (!isEmpty(cron)) {
            return newTrigger()
                    .withIdentity(buildName(), group)
                    .withSchedule(cronSchedule(cron)
                    		.withMisfireHandlingInstructionFireAndProceed()
                    		.inTimeZone(TimeZone.getTimeZone(systemDefault())))
                    .usingJobData("cron", cron)
                    .build();
        } else if (!isEmpty(fireTime)) {
        	JobDataMap jobDataMap = new JobDataMap();
        	jobDataMap.put("fireTime", fireTime);
            return newTrigger()
                    .withIdentity(buildName(), group)
                    .startAt(Date.from(fireTime.atZone(systemDefault()).toInstant()))
                    .usingJobData(jobDataMap)
                    .build();
        }
        // @formatter:on
		throw new IllegalStateException("unsupported trigger descriptor " + this);
	}

	public static TriggerDescriptor buildDescriptor(Trigger trigger) {
		// @formatter:off
        return new TriggerDescriptor()
                .setGroup(trigger.getKey().getGroup())
                .setName(trigger.getKey().getName())
                .setCron(trigger.getJobDataMap().getString("cron"))
                .setFireTime((LocalDateTime)trigger.getJobDataMap().get("fireTime"));
        // @formatter:on
	}
}
