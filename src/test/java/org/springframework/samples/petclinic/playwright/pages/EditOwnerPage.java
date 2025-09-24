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
package org.springframework.samples.petclinic.playwright.pages;

import com.microsoft.playwright.Page;

/**
 * Page Object Model for the Edit Owner page. Similar to AddOwnerPage but for editing
 * existing owners.
 */
public class EditOwnerPage extends BasePage {

	public EditOwnerPage(Page page) {
		super(page);
	}

	// Implementation similar to AddOwnerPage for editing owners

}