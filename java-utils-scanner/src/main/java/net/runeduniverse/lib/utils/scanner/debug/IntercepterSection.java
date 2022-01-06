/*
 * Copyright © 2022 Pl4yingNight (pl4yingnight@gmail.com)
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

import lombok.Getter;
import net.runeduniverse.lib.utils.logging.logs.CompoundTree;

@Getter
public class IntercepterSection implements IIntercepter {

	private final String id;
	private final CompoundTree tree;

	public IntercepterSection(String id, String headline) {
		this.id = id;
		this.tree = new CompoundTree(id, headline);
	}

	public URL intercept(URL url) {
		this.tree.append("URL", url.toString());
		return url;
	}

	public String intercept(String s) {
		this.tree.append(s);
		return s;
	}
}
