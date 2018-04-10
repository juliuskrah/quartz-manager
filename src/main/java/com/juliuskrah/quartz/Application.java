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
package com.juliuskrah.quartz;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.juliuskrah.quartz.autoconfigure.QuartzProperties;
import com.juliuskrah.quartz.mail.javamail.AsyncMailSender;

import io.github.jhipster.async.ExceptionHandlingAsyncTaskExecutor;
import lombok.RequiredArgsConstructor;

@EnableAsync
@SpringBootApplication
@RequiredArgsConstructor
@EnableConfigurationProperties({ QuartzProperties.class, MailProperties.class })
public class Application implements AsyncConfigurer {
	private final QuartzProperties quartzProperties;
	private final MailProperties mailProperties;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	private void applyProperties(JavaMailSenderImpl sender) {
		sender.setHost(this.mailProperties.getHost());
		if (this.mailProperties.getPort() != null) {
			sender.setPort(this.mailProperties.getPort());
		}
		sender.setUsername(this.mailProperties.getUsername());
		sender.setPassword(this.mailProperties.getPassword());
		sender.setProtocol(this.mailProperties.getProtocol());
		if (this.mailProperties.getDefaultEncoding() != null) {
			sender.setDefaultEncoding(this.mailProperties.getDefaultEncoding().name());
		}
		if (!this.mailProperties.getProperties().isEmpty()) {
			sender.setJavaMailProperties(asProperties(this.mailProperties.getProperties()));
		}
	}

	private Properties asProperties(Map<String, String> source) {
		Properties properties = new Properties();
		properties.putAll(source);
		return properties;
	}

	@Override
	@Bean(name = "taskExecutor")
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(quartzProperties.getAsync().getCorePoolSize());
		executor.setMaxPoolSize(quartzProperties.getAsync().getMaxPoolSize());
		executor.setQueueCapacity(quartzProperties.getAsync().getQueueCapacity());
		executor.setThreadNamePrefix("Email-");
		return new ExceptionHandlingAsyncTaskExecutor(executor);
	}

	@Bean
	public JavaMailSender mailSender() {
		JavaMailSenderImpl sender = new AsyncMailSender();
		applyProperties(sender);
		return sender;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new SimpleAsyncUncaughtExceptionHandler();
	}
}
