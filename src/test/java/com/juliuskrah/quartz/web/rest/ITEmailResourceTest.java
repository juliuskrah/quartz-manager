package com.juliuskrah.quartz.web.rest;

import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import com.juliuskrah.quartz.model.JobDescriptor;
import com.juliuskrah.quartz.model.TriggerDescriptor;
import com.juliuskrah.quartz.service.JobService;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@RunWith(SpringRunner.class)
public class ITEmailResourceTest {
	@Autowired
	private TestRestTemplate restTemplate;
	@Autowired
	private JobService emailService;
	@LocalServerPort
	int port;
	private JobDescriptor jobDetail;

	/**
	 * Builder to build the URL passed to the RestTemplate
	 * 
	 * @return {@link UriComponentsBuilder} containing the correct base URL
	 */
	private UriComponentsBuilder builder() {
		return UriComponentsBuilder.fromHttpUrl("http://localhost").port(this.port).path("/api/v1.0");
	}

	@Before
	public void init() {
		// Delete all Jobs created by the previous test
		emailService.deleteAllJobs();
		TriggerDescriptor trigger = new TriggerDescriptor().setName("test_name").setGroup("test_group").setFireTime(now().plusHours(1));

		jobDetail = new JobDescriptor().setName("test_name").setTo(asList("person@example.com", "anaother_person@example.com"))
				.setCc(asList("person_cc@example.com")).setBcc(asList("person_bcc@example.com", "anaother_person_bcc@example.com"))
				.setMessageBody("Testing a message body").setSubject("Test subject").setTriggerDescriptors(asList(trigger));

		TriggerDescriptor trigger1 = new TriggerDescriptor().setName("test_name1").setGroup("test_group").setFireTime(now().plusHours(1));

		JobDescriptor jobDetail1 = new JobDescriptor().setName("test_name1")
				.setTo(asList("person1@example.com", "anaother_person1@example.com")).setCc(asList("person1_cc@example.com"))
				.setBcc(asList("person1_bcc@example.com", "anaother_person1_bcc@example.com")).setMessageBody("Testing a message body1")
				.setSubject("Test subject1").setTriggerDescriptors(asList(trigger1));
		emailService.createJob("test_group", jobDetail1);
	}

	@Test
	public void testCreateJob() {
		ResponseEntity<Void> response = restTemplate.postForEntity(
				this.builder().path("/groups/{group}/jobs").buildAndExpand("test_group").toUri(), this.jobDetail, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(CREATED);
		assertThat(response.getHeaders().getLocation()).hasPath("/api/v1.0/groups/test_group/jobs/test_name");
	}

	@Test
	public void testFindGroupJobs() {
		RequestEntity<Void> request = RequestEntity.get(this.builder().path("/groups/{group}/jobs").buildAndExpand("test_group").toUri())
				.build();
		ParameterizedTypeReference<Set<JobDescriptor>> jobDetails = new ParameterizedTypeReference<Set<JobDescriptor>>() {
		};

		ResponseEntity<Set<JobDescriptor>> response = restTemplate.exchange(request, jobDetails);
		assertThat(response.getStatusCode()).isEqualTo(OK);
		assertThat(response.getBody()).hasOnlyElementsOfType(JobDescriptor.class);
		assertThat(response.getBody()).hasSize(1);
	}
}
