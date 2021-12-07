/*
 * Copyright © 2021 Pl4yingNight (pl4yingnight@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.runeduniverse.lib.utils.scanner.debug;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.runeduniverse.lib.utils.logging.logs.CompoundTree;

public class Intercepter implements IIntercepter {
	private final boolean active;
	private IntercepterSection baseSection;
	private Map<String, IntercepterSection> sections = new HashMap<>();

	public Intercepter(String headline, boolean active) {
		this.baseSection = new IntercepterSection(null, headline);
		this.active = active;
	}

	public IIntercepter addSection(IntercepterSection section) {
		this.sections.put(section.getId(), section);
		return section;
	}

	public IIntercepter addSection(String id, String headline) {
		return this.addSection(new IntercepterSection(id, headline));
	}

	@Override
	public String toString() {
		Set<String> keys = this.sections.keySet();
		CompoundTree tree = this.baseSection.getTree();
		for (String k : keys)
			tree.append(this.sections.get(k)
					.getTree());
		return tree.toString();
	}

	public void print() {
		if (!this.active)
			return;
		System.out.println(this.toString());
	}

	@Override
	public URL intercept(URL url) {
		return this.baseSection.intercept(url);
	}

	@Override
	public String intercept(String s) {
		return this.baseSection.intercept(s);
	}
}