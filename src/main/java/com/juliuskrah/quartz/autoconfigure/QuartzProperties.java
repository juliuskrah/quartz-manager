package com.juliuskrah.quartz.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties("com.juliuskrah.quartz")
@Setter
@Getter
public class QuartzProperties {
	private Resource configLocation;
	private final Async async = new Async();

	@Setter
	@Getter
	public static class Async {

		private int corePoolSize = 2;

		private int maxPoolSize = 50;

		private int queueCapacity = 10000;
	}
}
