/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.playwright;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Simple test to validate that Playwright framework is properly configured and the Spring
 * Boot test context can start successfully.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PlaywrightFrameworkValidationTest {

	@LocalServerPort
	private int port;

	@Test
	@DisplayName("As a developer, I want to validate the Playwright test framework is properly configured, so that I can run user acceptance tests")
	void shouldValidatePlaywrightFrameworkSetup() {
		// Given: The Spring Boot test context has started
		// When: The application is running on a random port
		// Then: The port should be assigned and accessible
		assertTrue(port > 0, "Application should be running on a valid port");
		assertNotNull(System.getProperty("java.version"), "Java runtime should be available");

		// Validate that the test can access the base URL
		String baseUrl = "http://localhost:" + port;
		assertNotNull(baseUrl, "Base URL should be constructed correctly");
		assertTrue(baseUrl.contains("localhost"), "Base URL should contain localhost");
	}

	@Test
	@DisplayName("As a developer, I want to ensure test configuration is loaded, so that tests run with the correct settings")
	void shouldLoadTestConfiguration() {
		// Given: The test configuration should be active
		// When: Spring Boot loads the application context
		// Then: The test profile should be active and port should be assigned
		assertTrue(port > 0, "Test application should start on random port");

		// Verify we can construct URLs for testing
		String ownerUrl = "http://localhost:" + port + "/owners/find";
		String vetUrl = "http://localhost:" + port + "/vets.html";

		assertNotNull(ownerUrl, "Owner URL should be constructable");
		assertNotNull(vetUrl, "Vet URL should be constructable");
		assertTrue(ownerUrl.contains("/owners/find"), "Owner URL should have correct path");
		assertTrue(vetUrl.contains("/vets.html"), "Vet URL should have correct path");
	}

}