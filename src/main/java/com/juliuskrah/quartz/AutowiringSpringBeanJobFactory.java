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

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * An implementation of {@code JobFactory} that supports Autowiring. After the
 * Job instance has been instantiated, all Autowired fields are injected.
 * 
 * @author Julius Krah
 * @since September 2017
 */
public class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {
	private transient AutowireCapableBeanFactory beanFactory;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		beanFactory = applicationContext.getAutowireCapableBeanFactory();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
		final Object job = super.createJobInstance(bundle);
		beanFactory.autowireBean(job);
		return job;
	}

}
