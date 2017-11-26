package com.juliuskrah.quartz.web.rest;

import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import java.util.Set;

import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
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
	/**
	 * Added this dependency:
	 * 
	 * <pre>
	 * {@code 
	 *   <dependency>
	 *     <groupId>org.apache.httpcomponents</groupId>
	 *     <artifactId>httpclient</artifactId> 
	 *   </dependency>
	 * }
	 * </pre>
	 * ensures that {@link RestTemplateBuilder#detectRequestFactory(boolean)} finds it.
	 */
	@Autowired
	private TestRestTemplate restTemplate;
	@Autowired
	private JobService emailService;
	@LocalServerPort
	int port;
	private JobDescriptor jobDetail;
	private JobDescriptor jobDetail1;

	/**
	 * Builder to build the URL passed to the RestTemplate
	 * 
	 * @return {@link UriComponentsBuilder} containing the correct base URL
	 */
	private UriComponentsBuilder builder() {
		return UriComponentsBuilder.fromHttpUrl("http://localhost").port(this.port).path("/api/v1.0");
	}

	@Before
	public void setup() {
		// Delete all Jobs created by the previous test
		emailService.deleteAllJobs();

		// @formatter:off
		TriggerDescriptor trigger = new TriggerDescriptor()
				.setName("test_name")
				.setGroup("test_group")
				.setFireTime(now().plusHours(1));

		this.jobDetail = new JobDescriptor()
				.setName("test_name")
				.setTo(asList("person@example.com", "another_person@example.com"))
				.setCc(asList("person_cc@example.com"))
				.setBcc(asList("person_bcc@example.com", "another_person_bcc@example.com"))
				.setMessageBody("Testing a message body")
				.setSubject("Test subject")
				.setTriggerDescriptors(asList(trigger));

		TriggerDescriptor trigger1 = new TriggerDescriptor()
				.setName("test_name1")
				.setGroup("test_group")
				.setFireTime(now().plusMinutes(1));

		this.jobDetail1 = new JobDescriptor()
				.setName("test_name1")
				.setTo(asList("person1@example.com", "another_person1@example.com"))
				.setCc(asList("person1_cc@example.com"))
				.setBcc(asList("person1_bcc@example.com", "another_person1_bcc@example.com"))
				.setMessageBody("Testing a message body1")
				.setSubject("Test subject1")
				.setTriggerDescriptors(asList(trigger1));
		
		TriggerDescriptor trigger2 = new TriggerDescriptor()
				.setName("test_name2")
				.setGroup("test_group1")
				.setFireTime(now().plusMinutes(1));

		JobDescriptor jobDetail2 = new JobDescriptor()
				.setName("test_name2")
				.setTo(asList("person2@example.com", "another_person2@example.com"))
				.setCc(asList("person2_cc@example.com"))
				.setBcc(asList("person2_bcc@example.com", "another_person2_bcc@example.com"))
				.setMessageBody("Testing a message body2")
				.setSubject("Test subject2")
				.setTriggerDescriptors(asList(trigger2));
		
		emailService.createJob("test_group", this.jobDetail1);
		emailService.createJob("test_group2", jobDetail2);
		// @formatter:on
	}

	@Test
	public void testCreateJob() {
		ResponseEntity<Void> response = restTemplate.postForEntity(
				this.builder().path("/groups/{group}/jobs").buildAndExpand("test_group").toUri(), this.jobDetail, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(CREATED);
		assertThat(response.getHeaders().getLocation()).hasPath("/api/v1.0/groups/test_group/jobs/test_name");
	}

	@Test
	public void testCreateJobIs409() {
		ResponseEntity<Void> response = restTemplate.postForEntity(
				this.builder().path("/groups/{group}/jobs").buildAndExpand("test_group").toUri(), this.jobDetail1, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(CONFLICT);
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

	@Test
	public void testFindJobs() {
		RequestEntity<Void> request = RequestEntity.get(this.builder().path("/jobs").buildAndExpand("test_group").toUri()).build();
		ParameterizedTypeReference<Set<JobDescriptor>> jobDetails = new ParameterizedTypeReference<Set<JobDescriptor>>() {
		};

		ResponseEntity<Set<JobDescriptor>> response = restTemplate.exchange(request, jobDetails);
		assertThat(response.getStatusCode()).isEqualTo(OK);
		assertThat(response.getBody()).hasOnlyElementsOfType(JobDescriptor.class);
		assertThat(response.getBody()).hasSize(2);
	}

	@Test
	public void testFindJob() {
		ResponseEntity<JobDescriptor> response = restTemplate.getForEntity(
				this.builder().path("/groups/{group}/jobs/{name}").buildAndExpand("test_group", "test_name1").toUri(), JobDescriptor.class);
		assertThat(response.getStatusCode()).isEqualTo(OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getTo()).areExactly(2, new Condition<String>() {

			@Override
			public boolean matches(String value) {
				return value.endsWith("example.com");
			}
		});
	}

	@Test
	public void testFindJobIs404() {
		ResponseEntity<JobDescriptor> response = restTemplate.getForEntity(
				this.builder().path("/groups/{group}/jobs/{name}").buildAndExpand("test_group", "test_name").toUri(), JobDescriptor.class);
		assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
	}

	@Test
	public void testUpdateJob() {
		JobDescriptor jobDetail = new JobDescriptor().setName("test_name2")
				.setTo(asList("update_person2@example.com", "update_another_person2@example.com"))
				.setCc(asList("update_person2_cc@example.com"))
				.setBcc(asList("update_person2_bcc@example.com", "another_person2_bcc@example.com"))
				.setMessageBody("Testing a message body2 updated").setSubject("Test subject2 updated");
		RequestEntity<JobDescriptor> request = RequestEntity
				.put(this.builder().path("/groups/{group}/jobs/{name}").buildAndExpand("test_group", "test_name1").toUri()).body(jobDetail);

		ResponseEntity<Void> response = restTemplate.exchange(request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
	}

	@Test
	public void testDeleteJob() {
		RequestEntity<Void> request = RequestEntity
				.delete(this.builder().path("/groups/{group}/jobs/{name}").buildAndExpand("test_group", "test_name1").toUri()).build();

		ResponseEntity<Void> response = restTemplate.exchange(request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
	}

	@Test
	public void testPauseJob() {
		RequestEntity<Void> request = RequestEntity
				.patch(this.builder().path("/groups/{group}/jobs/{name}/pause").buildAndExpand("test_group", "test_name1").toUri()).build();

		ResponseEntity<Void> response = restTemplate.exchange(request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
	}

	@Test
	public void testResumeJob() {
		RequestEntity<Void> request = RequestEntity
				.patch(this.builder().path("/groups/{group}/jobs/{name}/resume").buildAndExpand("test_group", "test_name1").toUri()).build();

		ResponseEntity<Void> response = restTemplate.exchange(request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
	}
}
