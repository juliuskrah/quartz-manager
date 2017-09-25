package com.juliuskrah.quartz.job;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
// @RequiredArgsConstructor
public class EmailJob implements Job {
	// private final MailSender mailSender;
	// private final SimpleMailMessage mailMessage;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap map = context.getMergedJobDataMap();
		log.info("Map: {}", map.getWrappedMap());
	}

}
