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

package org.springframework.samples.petclinic.uat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.PetTypeRepository;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 * Base class for User Acceptance Tests.
 *
 * Provides common infrastructure for UATs including: - Spring Boot test context with
 * random port - RestTemplate for API testing - Access to repositories for test data setup
 * - Transactional rollback for test isolation
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public abstract class BaseUserAcceptanceTest {

	@LocalServerPort
	protected int port;

	@Autowired
	protected OwnerRepository ownerRepository;

	@Autowired
	protected VetRepository vetRepository;

	@Autowired
	protected PetTypeRepository petTypeRepository;

	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	protected RestTemplate restTemplate() {
		return restTemplateBuilder.rootUri("http://localhost:" + port).build();
	}

	protected String baseUrl() {
		return "http://localhost:" + port;
	}

}