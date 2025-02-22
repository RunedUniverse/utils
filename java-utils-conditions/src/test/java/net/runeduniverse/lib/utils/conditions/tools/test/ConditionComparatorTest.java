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
package net.runeduniverse.lib.utils.conditions.tools.test;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import net.runeduniverse.lib.utils.conditions.test.model.MvnGoalView;
import net.runeduniverse.lib.utils.conditions.tools.ConditionComparator;
import net.runeduniverse.lib.utils.conditions.tools.EntrySet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static net.runeduniverse.lib.utils.conditions.test.model.ModelFactory.*;

public class ConditionComparatorTest {

	@Test
	@Tag("system")
	public void testCompareWithBeforeNoPriority() {
		final EntrySet<MvnGoalView> set = new EntrySet<>();
		// match: mycila | before: revelc | mycila > revelc
		set.add(createGoalConditionMycilaFormatter(), createGoalConditionRevelcFormatter(), null);

		final ConditionComparator<MvnGoalView> comparator = new ConditionComparator<MvnGoalView>(set);

		final MvnGoalView viewRevelc = createGoalViewRevelcFormatter();
		final MvnGoalView viewMycila = createGoalViewMycilaFormatter();

		assertEquals(0, comparator.compare(viewRevelc, viewRevelc));
		assertEquals(0, comparator.compare(viewMycila, viewMycila));

		assertEquals(-1, comparator.compare(viewRevelc, viewMycila));
		assertEquals(1, comparator.compare(viewMycila, viewRevelc));
	}

	@Test
	@Tag("system")
	public void testCompareWithAfterNoPriority() {
		final EntrySet<MvnGoalView> set = new EntrySet<>();
		// match: revelc | after: mycila | revelc < mycila
		set.add(createGoalConditionRevelcFormatter(), null, createGoalConditionMycilaFormatter());

		final ConditionComparator<MvnGoalView> comparator = new ConditionComparator<MvnGoalView>(set);

		final MvnGoalView viewRevelc = createGoalViewRevelcFormatter();
		final MvnGoalView viewMycila = createGoalViewMycilaFormatter();

		assertEquals(0, comparator.compare(viewRevelc, viewRevelc));
		assertEquals(0, comparator.compare(viewMycila, viewMycila));

		assertEquals(-1, comparator.compare(viewRevelc, viewMycila));
		assertEquals(1, comparator.compare(viewMycila, viewRevelc));

	}

	@Test
	@Tag("system")
	public void testCompareWithBeforeWithPriority() {
		final EntrySet<MvnGoalView> set = new EntrySet<>();
		// match: revelc | before: mycila | priority: 0 | revelc > mycila
		set.add(createGoalConditionMycilaFormatter(), createGoalConditionRevelcFormatter(0), null);
		// match: mycila | before: revelc | priority: 1 | mycila > revelc
		set.add(createGoalConditionMycilaFormatter(), createGoalConditionRevelcFormatter(1), null);

		final ConditionComparator<MvnGoalView> comparator = new ConditionComparator<MvnGoalView>(set);

		final MvnGoalView viewRevelc = createGoalViewRevelcFormatter();
		final MvnGoalView viewMycila = createGoalViewMycilaFormatter();

		assertEquals(0, comparator.compare(viewRevelc, viewRevelc));
		assertEquals(0, comparator.compare(viewMycila, viewMycila));

		assertEquals(-1, comparator.compare(viewRevelc, viewMycila));
		assertEquals(1, comparator.compare(viewMycila, viewRevelc));
	}

	@Test
	@Tag("system")
	public void testCompareWithAfterWithPriority() {
		final EntrySet<MvnGoalView> set = new EntrySet<>();
		// match: mycila | after: revelc | priority: 0 | mycila < revelc
		set.add(createGoalConditionMycilaFormatter(), null, createGoalConditionRevelcFormatter(0));
		// match: revelc | after: mycila | priority: 1 | revelc < mycila
		set.add(createGoalConditionRevelcFormatter(), null, createGoalConditionMycilaFormatter(1));

		final ConditionComparator<MvnGoalView> comparator = new ConditionComparator<MvnGoalView>(set);

		final MvnGoalView viewRevelc = createGoalViewRevelcFormatter();
		final MvnGoalView viewMycila = createGoalViewMycilaFormatter();

		assertEquals(0, comparator.compare(viewRevelc, viewRevelc));
		assertEquals(0, comparator.compare(viewMycila, viewMycila));

		assertEquals(-1, comparator.compare(viewRevelc, viewMycila));
		assertEquals(1, comparator.compare(viewMycila, viewRevelc));
	}

	@Test
	@Tag("system")
	public void testCompareWithInternalCollisionNoPriority() {
		final EntrySet<MvnGoalView> set = new EntrySet<>();
		// match: mycila | before: revelc | after: revelc | revelc < mycila < revelc =>
		// forced equal
		set.add(createGoalConditionMycilaFormatter(), createGoalConditionRevelcFormatter(),
				createGoalConditionRevelcFormatter());

		final ConditionComparator<MvnGoalView> comparator = new ConditionComparator<MvnGoalView>(set);

		final MvnGoalView viewRevelc = createGoalViewRevelcFormatter();
		final MvnGoalView viewMycila = createGoalViewMycilaFormatter();

		assertEquals(0, comparator.compare(viewRevelc, viewRevelc));
		assertEquals(0, comparator.compare(viewMycila, viewMycila));

		assertEquals(0, comparator.compare(viewRevelc, viewMycila));
		assertEquals(0, comparator.compare(viewMycila, viewRevelc));
	}

	@Test
	@Tag("system")
	public void testCompareWithExternalCollisionNoPriority() {
		final EntrySet<MvnGoalView> set = new EntrySet<>();
		// match: mycila | before: revelc | mycila > revelc
		set.add(createGoalConditionMycilaFormatter(), createGoalConditionRevelcFormatter(), null);
		// match: mycila | after: revelc | mycila < revelc
		set.add(createGoalConditionMycilaFormatter(), null, createGoalConditionRevelcFormatter());
		// => forced equal

		final ConditionComparator<MvnGoalView> comparator = new ConditionComparator<MvnGoalView>(set);

		final MvnGoalView viewRevelc = createGoalViewRevelcFormatter();
		final MvnGoalView viewMycila = createGoalViewMycilaFormatter();

		assertEquals(0, comparator.compare(viewRevelc, viewRevelc));
		assertEquals(0, comparator.compare(viewMycila, viewMycila));

		assertEquals(0, comparator.compare(viewRevelc, viewMycila));
		assertEquals(0, comparator.compare(viewMycila, viewRevelc));
	}

	@Test
	@Tag("system")
	public void testCompareWithEmptySet() {
		final EntrySet<MvnGoalView> set = new EntrySet<>();
		// => forced equal

		final ConditionComparator<MvnGoalView> comparator = new ConditionComparator<MvnGoalView>(set);

		final MvnGoalView viewRevelc = createGoalViewRevelcFormatter();
		final MvnGoalView viewMycila = createGoalViewMycilaFormatter();

		assertEquals(0, comparator.compare(viewRevelc, viewRevelc));
		assertEquals(0, comparator.compare(viewMycila, viewMycila));

		assertEquals(0, comparator.compare(viewRevelc, viewMycila));
		assertEquals(0, comparator.compare(viewMycila, viewRevelc));
	}
}
