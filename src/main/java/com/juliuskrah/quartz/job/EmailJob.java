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
package com.juliuskrah.quartz.job;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.juliuskrah.quartz.service.AsyncMailSender;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * This is an implementation of {@link Job} that executes when triggered to send
 * emails. This job retrieves the list of emails, cc and bcc from the
 * {@code JobDetail} {@code JobDataMap} and passes them to the
 * {@code JavaMailSender} for SMTP transport.
 * 
 * There setter methods defined that correspond to the keys stored in the
 * JobDateMap that the {@code JobFactory} implementation will set when
 * instantiating this Job.
 * 
 * @author Julius Krah
 * @since September 2017
 */
@Slf4j
@Setter
public class EmailJob implements Job {
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private AsyncMailSender asyncMailSender;
	private String subject;
	private String messageBody;
	private List<String> to;
	private List<String> cc;
	private List<String> bcc;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("Job triggered to send email to {}", to);
		sendEmail();
		log.info("Job completed");
	}

	/**
	 * Iterates through the list of email and sends email to recipients
	 */
	private void sendEmail() {
		MimeMessage message = mailSender.createMimeMessage();

		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, false);
			for (String receipient : to) {
				helper.setFrom("jk@juliuskrah.com", "Julius from Dynamic Quartz");
				helper.setTo(receipient);
				helper.setSubject(subject);
				helper.setText(messageBody);
				if (!isEmpty(cc))
					helper.setCc(cc.stream().toArray(String[]::new));
				if (!isEmpty(bcc))
					helper.setBcc(bcc.stream().toArray(String[]::new));
				asyncMailSender.send(message);
			}
		} catch (MessagingException | UnsupportedEncodingException e) {
			log.error("An error occurred: {}", e.getLocalizedMessage());
		}

	}

}
