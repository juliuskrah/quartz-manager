package com.juliuskrah.quartz.job;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
public class EmailJob implements Job {
	@Autowired
	private JavaMailSender mailSender;
	private String subject;
	private String messageBody;
	private List<String> to;
	private List<String> cc;
	private List<String> bcc;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("Job triggered to send email to {}", to);
		sendEmail();
		log.info("Job completed");
	}
	
	private void sendEmail() {
		MimeMessage message = mailSender.createMimeMessage();

		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, false);
			for(String receipient : to) {
				helper.setTo(receipient);
				helper.setSubject(subject);
				helper.setText(messageBody);
				if(!isEmpty(cc))
					helper.setCc(cc.stream().toArray(String[]::new));
				if(!isEmpty(bcc))
					helper.setBcc(bcc.stream().toArray(String[]::new));
				mailSender.send(message);
			}
		} catch (MessagingException e) {
			log.error("An error occurred: {}", e.getLocalizedMessage());
		}
		
	}

}
