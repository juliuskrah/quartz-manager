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

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.PATCH;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

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
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.juliuskrah.quartz.autoconfigure.QuartzProperties;
import com.juliuskrah.quartz.handler.EmailHandler;
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

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new SimpleAsyncUncaughtExceptionHandler();
	}

	@Bean
	public JavaMailSender mailSender() {
		JavaMailSenderImpl sender = new AsyncMailSender();
		applyProperties(sender);
		return sender;
	}

	@Bean
	public RouterFunction<ServerResponse> emailRouter(EmailHandler emailHandler) {
		// @formatter:off
		return nest(path("/api/v1.0"),
				// POST /api/v1.0/groups/:group/jobs
				route(POST("/groups/{group}/jobs").and(contentType(APPLICATION_JSON)), emailHandler::createJob)
				// GET /api/v1.0/groups/:group/jobs
				.andRoute(GET("/groups/{group}/jobs").and(accept(APPLICATION_JSON)), emailHandler::findGroupJobs)	
				// GET /api/v1.0/jobs
				.andRoute(GET("/jobs").and(accept(APPLICATION_JSON)), emailHandler::findJobs)
				// GET /api/v1.0/groups/:group/jobs/:name
				.andRoute(GET("/groups/{group}/jobs/{name}").and(accept(APPLICATION_JSON)), emailHandler::findJob)
				// PUT /api/v1.0/groups/:group/jobs/:name
				.andRoute(PUT("/groups/{group}/jobs/{name}").and(contentType(APPLICATION_JSON)), emailHandler::updateJob)
				// DELETE /api/v1.0/groups/:group/jobs/:name
				.andRoute(DELETE("/groups/{group}/jobs/{name}"), emailHandler::deleteJob)
				// PATCH /api/v1.0/groups/:group/jobs/:name/pause
				.andRoute(PATCH("/groups/{group}/jobs/{name}/pause"), emailHandler::pauseJob)
				// PATCH /api/v1.0/groups/:group/jobs/:name/resume
				.andRoute(PATCH("/groups/{group}/jobs/{name}/resume"), emailHandler::resumeJob)
		);
		// @formatter:on

	}
}
