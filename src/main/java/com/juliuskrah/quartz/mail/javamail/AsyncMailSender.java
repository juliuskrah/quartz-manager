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
package com.juliuskrah.quartz.mail.javamail;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;

import lombok.extern.slf4j.Slf4j;

/**
 * Asynchronous mail sender
 * 
 * @author Julius Krah
 */
@Slf4j
public class AsyncMailSender extends JavaMailSenderImpl {
	
	@Async
	@Override
	public void send(MimeMessage mimeMessage) {
		log.debug("Starting async mail send");
		super.send(mimeMessage);
		log.debug("Async mail sent");
	}

	@Async
	@Override
	public void send(MimeMessage... mimeMessages) {
		log.debug("Starting async mail send");
		super.send(mimeMessages);
		log.debug("Async mail sent");
	}
}
