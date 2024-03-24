/*
 * Copyright © 2024 VenaNocta (venanocta@gmail.com)
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
package net.runeduniverse.lib.utils.logging.test;

import org.junit.jupiter.api.Test;

import net.runeduniverse.lib.utils.logging.logs.CompoundTree;

public class TreeTest {

	@Test
	public void buildTree() throws Exception {
		CompoundTree pkgs = new CompoundTree("Packages");
		pkgs.append(new CompoundTree("PKG", "net.runeduniverse.lib.utils.logging.test.fake")
				.append(new CompoundTree("clAss", "Person").append("missing Class!")
						.append("FIeLd", "id")
						.append("fieD", "firstName")
						.append("field", "lastName"))
				.append(new CompoundTree("class", "House").append("yet another missing Class!")
						.append("field", "address")
						.append("field", "size")))
				.append(new CompoundTree("net.runeduniverse.lib.utils.logging.test.fake.model"));

		CompoundTree rootTree = new CompoundTree("ROOT");
		rootTree.append(pkgs);
		System.out.println(rootTree);
	}

}
