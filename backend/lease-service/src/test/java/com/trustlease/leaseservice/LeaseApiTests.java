package com.trustlease.leaseservice;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.trustlease.leaseservice.models.Lease;
import com.trustlease.leaseservice.repository.LeaseRepository;
import com.trustlease.leaseservice.shared_contracts.LeasePermission;
import com.trustlease.leaseservice.shared_contracts.LeaseStatus;

@Testcontainers
@SpringBootTest(
		properties = {
				"spring.docker.compose.enabled=false",
				"spring.flyway.enabled=false",
				"spring.jpa.hibernate.ddl-auto=create-drop",
				"spring.sql.init.mode=never"
		})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class LeaseApiTests {

	private static final String BASE_URL = "/api/v1/leases";

	@Container
	private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18.6-alpine")
			.withDatabaseName("lease_test")
			.withUsername("lease_test")
			.withPassword("lease_test");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private LeaseRepository leaseRepository;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private Long testLeaseId;
	private Long createdLeaseId;

	@DynamicPropertySource
	static void configureDatabase(DynamicPropertyRegistry properties) {
		properties.add("spring.datasource.url", POSTGRES::getJdbcUrl);
		properties.add("spring.datasource.username", POSTGRES::getUsername);
		properties.add("spring.datasource.password", POSTGRES::getPassword);
	}

	@BeforeEach
	void createLeaseFixture() {
		Lease lease = new Lease();
		lease.setOrgId(7L);
		lease.setResourceId(101L);
		lease.setSubjectId(201L);
		lease.setPermissions(Set.of(LeasePermission.READ));
		lease.setExpiresAt(Instant.parse("2030-01-01T00:00:00Z"));
		lease.setMaxUses(10L);
		lease.setStatus(LeaseStatus.ACTIVE);

		testLeaseId = leaseRepository.saveAndFlush(lease).getId();
	}

	@AfterEach
	void removeLeaseFixture() {
		if (createdLeaseId != null) {
			leaseRepository.deleteById(createdLeaseId);
		}
		if (testLeaseId != null) {
			leaseRepository.deleteById(testLeaseId);
		}
	}

	@Test
	void healthReturnsServiceStatus() throws Exception {
		mockMvc.perform(get(BASE_URL + "/health"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("UP"))
				.andExpect(jsonPath("$.service").value("lease-service"));
	}

	@Test
	void listReturnsPersistedLeasesAndPageMetadata() throws Exception {
		mockMvc.perform(get(BASE_URL + "/"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.page").value(0))
				.andExpect(jsonPath("$.size").value(20))
				.andExpect(jsonPath("$.totalItems").value(1))
				.andExpect(jsonPath("$.items[0].id").value(testLeaseId))
				.andExpect(jsonPath("$.items[0].orgId").value(7))
				.andExpect(jsonPath("$.items[0].resourceId").value(101))
				.andExpect(jsonPath("$.items[0].permissions[0]").value("READ"))
				.andExpect(jsonPath("$.items[0].status").value("ACTIVE"));
	}

	@Test
	void getLeaseReturnsPersistedLeaseById() throws Exception {
		mockMvc.perform(get(BASE_URL + "/" + testLeaseId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(testLeaseId))
				.andExpect(jsonPath("$.orgId").value(7))
				.andExpect(jsonPath("$.resourceId").value(101))
				.andExpect(jsonPath("$.subjectId").value(201))
				.andExpect(jsonPath("$.maxUses").value(10))
				.andExpect(jsonPath("$.remainingUses").doesNotExist())
				.andExpect(jsonPath("$.status").value("ACTIVE"));
	}

	@Test
	void createLeaseBindsFormFieldsAndReturnsCreatedLease() throws Exception {
		String expiresAt = "2030-02-01T12:30:00Z";
		mockMvc.perform(post(BASE_URL + "/")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param("orgId", "8")
						.param("resourceId", "102")
						.param("subjectId", "202")
						.param("permissions", "READ")
						.param("expiresAt", expiresAt)
						.param("maxUses", "5"))
				.andDo(result -> createdLeaseId = objectMapper
						.readTree(result.getResponse().getContentAsString()).get("id").asLong())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.orgId").value(8))
				.andExpect(jsonPath("$.resourceId").value(102))
				.andExpect(jsonPath("$.subjectId").value(202))
				.andExpect(jsonPath("$.permissions[0]").value("READ"))
				.andExpect(jsonPath("$.expiresAt").value(expiresAt))
				.andExpect(jsonPath("$.maxUses").value(5));
	}

	@Test
	void createLeaseRejectsNonPositiveOrgId() throws Exception {
		mockMvc.perform(post(BASE_URL + "/")
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.param("orgId", "0")
					.param("resourceId", "102")
					.param("subjectId", "202")
					.param("permissions", "READ")
					.param("expiresAt", "2030-02-01T12:30:00Z")
					.param("maxUses", "5"))
				.andExpect(status().isBadRequest());
	}
}
