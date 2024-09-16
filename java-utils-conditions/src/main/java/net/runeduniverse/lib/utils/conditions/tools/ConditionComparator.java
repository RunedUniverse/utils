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
package net.runeduniverse.lib.utils.conditions.tools;

import java.util.Comparator;
import net.runeduniverse.lib.utils.conditions.api.Condition;

public class ConditionComparator<T> implements Comparator<T> {

	protected final EntrySet<T> entries;

	public ConditionComparator(EntrySet<T> entries) {
		this.entries = entries;
	}

	@Override
	public int compare(T o1, T o2) {
		Entry<T> e1 = null, e2 = null;
		for (Entry<T> entry : this.entries) {
			if (e1 == null)
				e1 = checkMatch(entry, o1);
			if (e2 == null)
				e2 = checkMatch(entry, o2);
			if (e1 == null && e2 == null)
				break;
		}

		if (e1 == e2)
			return 0;

		int result = compare(e1, o2);
		if (result != 0)
			return result;
		return compare(e2, o1) * -1;
	}

	protected int compare(final Entry<T> entry, final T entity) {
		final Condition<T> before = entry.getMatchBefore();
		if (before != null && before.evaluate(entity)) {
			return 1;
		}
		final Condition<T> after = entry.getMatchAfter();
		if (after != null && after.evaluate(entity)) {
			return -1;
		}
		return 0;
	}

	protected Entry<T> checkMatch(final Entry<T> entry, final T entity) {
		final Condition<T> con = entry.getMatchItem();
		if (con == null)
			return null;
		if (con.evaluate(entity))
			return entry;
		return null;
	}
}
