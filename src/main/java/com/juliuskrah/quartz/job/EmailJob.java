package com.juliuskrah.quartz.job;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailJob implements Job {
	@Autowired
	private JavaMailSender mailSender;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap map = context.getMergedJobDataMap();
		log.info("Map: {}", map.getWrappedMap());
		sendEmail(map);
	}
	
	private void sendEmail(JobDataMap map) {
		
	}

}
