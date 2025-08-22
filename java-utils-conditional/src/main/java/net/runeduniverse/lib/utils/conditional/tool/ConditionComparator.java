/*
 * Copyright © 2025 VenaNocta (venanocta@gmail.com)
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
package net.runeduniverse.lib.utils.conditional.tool;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import net.runeduniverse.lib.utils.conditional.api.Condition;

public class ConditionComparator<T> implements Comparator<T> {

	protected final RelationEntrySet<T> entries;

	public ConditionComparator(final RelationEntrySet<T> entries) {
		this.entries = entries;
	}

	@Override
	public int compare(final T o1, final T o2) {
		Objects.requireNonNull(o1);
		Objects.requireNonNull(o2);
		if (o1.equals(o2))
			return 0;

		final Set<RelationEntry<T>> e1 = new LinkedHashSet<>(), e2 = new LinkedHashSet<>();
		for (RelationEntry<T> entry : this.entries) {
			if (checkMatch(entry, o1))
				e1.add(entry);
			if (checkMatch(entry, o2))
				e2.add(entry);
		}

		if (e1.isEmpty() && e2.isEmpty())
			return 0;

		if (e1.size() == e2.size()) {
			final Set<RelationEntry<T>> e = new LinkedHashSet<>();
			e.addAll(e1);
			e.addAll(e2);
			if (e1.containsAll(e) && e2.containsAll(e))
				return 0;
		}

		final PriorityResult r1 = compare(toPriorityTree(e1, RelationEntry::getMatchBefore),
				toPriorityTree(e1, RelationEntry::getMatchAfter), o2);
		final PriorityResult r2 = compare(toPriorityTree(e2, RelationEntry::getMatchBefore),
				toPriorityTree(e2, RelationEntry::getMatchAfter), o1);

		return computeExternalResult(r1, r2);
	}

	protected PriorityResult compare(final Map<Integer, Set<Condition<T>>> beforeMap,
			final Map<Integer, Set<Condition<T>>> afterMap, final T entity) {
		final TreeSet<Integer> priorities = new TreeSet<>();
		priorities.addAll(beforeMap.keySet());
		priorities.addAll(afterMap.keySet());

		for (Iterator<Integer> i = priorities.descendingIterator(); i.hasNext();) {
			final Integer p = i.next();
			boolean before = false, after = false;
			Set<Condition<T>> set = beforeMap.get(p);
			if (set != null) {
				for (Condition<T> con : set)
					if (con != null && con.evaluate(entity)) {
						before = true;
						break;
					}
			}
			set = afterMap.get(p);
			if (set != null) {
				for (Condition<T> con : set)
					if (con != null && con.evaluate(entity)) {
						after = true;
						break;
					}
			}
			final int result = computeInternalResult(before, after);
			if (result != 0)
				return new PriorityResult(p, result);
		}
		return null;
	}

	protected int computeInternalResult(final boolean before, final boolean after) {
		if (before == after)
			return 0;
		if (before) {
			if (after) {
				// collision => force equal
				return 0;
			}
			return 1;
		}
		if (after)
			return -1;
		return 0;
	}

	protected int computeExternalResult(final PriorityResult pr1, final PriorityResult pr2) {
		if (pr1 == null) {
			if (pr2 == null)
				return 0;
			return pr2.getResult() * -1;
		}
		if (pr2 == null)
			return pr1.getResult();

		final int p1 = pr1.getPriority(), p2 = pr2.getPriority();
		final int r1 = pr1.getResult(), r2 = pr2.getResult() * -1;
		if (p1 == p2) {
			if (r1 == r2)
				return r1;
			// collision: => force equal
			return 0;
		}
		return p1 > p2 ? r1 : r2;
	}

	protected boolean checkMatch(final RelationEntry<T> entry, final T entity) {
		final Condition<T> con = entry.getMatchItem();
		if (con == null)
			return false;
		return con.evaluate(entity);
	}

	protected Map<Integer, Set<Condition<T>>> toPriorityTree(final Set<RelationEntry<T>> set,
			final ConditionSelector<T> selector) {
		final Map<Integer, Set<Condition<T>>> map = new LinkedHashMap<>();
		for (RelationEntry<T> entry : set) {
			final Condition<T> con = selector.get(entry);
			if (con == null)
				continue;
			final Set<Condition<T>> s = map.computeIfAbsent(con.getPriority(), k -> new LinkedHashSet<>());
			s.add(con);
		}
		return map;
	}

	@FunctionalInterface
	protected interface ConditionSelector<T> {

		public Condition<T> get(final RelationEntry<T> entry);

	}

	protected static class PriorityResult {

		final int p, result;

		public PriorityResult(final int p, final int result) {
			this.p = p;
			this.result = result;
		}

		public int getPriority() {
			return this.p;
		}

		public int getResult() {
			return this.result;
		}
	}
}
