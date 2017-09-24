package com.juliuskrah.quartz;

import org.quartz.SchedulerException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@SpringBootApplication
public class Application {

	public static void main(String[] args) throws SchedulerException {
		SpringApplication.run(Application.class, args);
	}
	
	@Bean
	public SchedulerFactoryBean schedulerFactory() {
		SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
		return factoryBean;
	}
}
