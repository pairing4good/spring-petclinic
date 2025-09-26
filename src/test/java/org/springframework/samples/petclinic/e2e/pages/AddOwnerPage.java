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
package org.springframework.samples.petclinic.e2e.pages;

/**
 * Page Object Model for the Add Owner page of the Pet Clinic application. Provides
 * methods to create new owners with form validation.
 */
public class AddOwnerPage extends BasePage {

	public AddOwnerPage(String baseUrl) {
		super(baseUrl);
		navigateTo("/owners/new");
	}

	// TODO: Implement Add Owner page functionality
	// - Form fields (firstName, lastName, address, city, telephone)
	// - Form validation
	// - Submit and cancel actions

}