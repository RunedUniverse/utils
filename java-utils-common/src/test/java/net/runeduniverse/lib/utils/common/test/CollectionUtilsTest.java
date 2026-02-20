/*
 * Copyright © 2026 VenaNocta (venanocta@gmail.com)
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
package net.runeduniverse.lib.utils.common.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import net.runeduniverse.lib.utils.common.CollectionUtils;

public class CollectionUtilsTest {

	protected final List<String> list1;
	protected final List<String> list2;

	protected final Set<String> set1;

	protected final Map<Integer, String> map1;

	public CollectionUtilsTest() {
		// seed test data

		this.list1 = Arrays.asList(//
				"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", //
				"n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z");
		this.list2 = Arrays.asList(//
				null, "a", "b", "c", "d", "e", null, "f", "g", "h", "i", "j", "k", "l", "m", //
				"n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", null, "z", null, null);

		this.set1 = new HashSet<String>(this.list1);

		this.map1 = new HashMap<>();
		for (int i = 0; i < 26; i++) {
			this.map1.put(i, "" + (char) ('a' + i));
		}
	}

	@Test
	@Tag("system")
	public void first_1() throws InterruptedException {
		Assertions.assertEquals("a", CollectionUtils.first(this.list1));
	}

	@Test
	@Tag("system")
	public void firstNotNull_1() throws InterruptedException {
		Assertions.assertEquals("a", CollectionUtils.firstNotNull(this.list2));
	}

	@Test
	@Tag("system")
	public void last_1() throws InterruptedException {
		Assertions.assertEquals("z", CollectionUtils.last(this.list1));
	}

	@Test
	@Tag("system")
	public void lastNotNull_1() throws InterruptedException {
		Assertions.assertEquals("z", CollectionUtils.lastNotNull(this.list2));
	}

	@Test
	@Tag("system")
	public void copy_list_1() throws InterruptedException {
		List<?> obj = CollectionUtils.copy(this.list1, LinkedList::new);

		Assertions.assertInstanceOf(LinkedList.class, obj);
		Assertions.assertTrue(this.list1.containsAll(obj) && obj.containsAll(this.list1));
	}

	@Test
	@Tag("system")
	public void copy_set_1() throws InterruptedException {
		Set<?> obj = CollectionUtils.copy(this.set1, LinkedHashSet::new);

		Assertions.assertInstanceOf(LinkedHashSet.class, obj);
		Assertions.assertTrue(this.set1.containsAll(obj) && obj.containsAll(this.set1));
	}

	@Test
	@Tag("system")
	public void copy_map_1() throws InterruptedException {
		Map<Integer, String> obj = CollectionUtils.copy(this.map1, LinkedHashMap::new);

		Assertions.assertInstanceOf(LinkedHashMap.class, obj);

		Set<Entry<Integer, String>> entriesMap1 = this.map1.entrySet();
		Set<Entry<Integer, String>> entriesObj = this.map1.entrySet();
		Assertions.assertTrue(entriesMap1.containsAll(entriesObj) && entriesObj.containsAll(entriesMap1));

		Assertions.assertEquals("e", this.map1.get(4));
		Assertions.assertEquals("e", obj.get(4));
		Assertions.assertEquals("z", this.map1.get(25));
		Assertions.assertEquals("z", obj.get(25));
		Assertions.assertEquals(null, this.map1.get(26));
		Assertions.assertEquals(null, obj.get(26));
	}
}
