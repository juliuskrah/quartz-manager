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

import java.util.concurrent.CompletableFuture;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Async
@Service
@RequiredArgsConstructor
public class AsyncMailSender {
	private final JavaMailSender mailSender;

	public CompletableFuture<String> send(MimeMessage message) {
		log.debug("Starting async mail send");
		mailSender.send(message);
		log.debug("Async mail sent");
		return CompletableFuture.completedFuture("Mail sent successfully");
	}

	public CompletableFuture<String> send(MimeMessage... message) {
		log.debug("Starting async mail send");
		mailSender.send(message);
		log.debug("Async mail sent");
		return CompletableFuture.completedFuture("Mail sent successfully");
	}
}
