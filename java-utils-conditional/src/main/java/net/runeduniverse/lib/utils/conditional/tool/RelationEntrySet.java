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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import net.runeduniverse.lib.utils.conditional.api.Condition;

public class RelationEntrySet<T> extends LinkedHashSet<RelationEntry<T>> {

	private static final long serialVersionUID = 1L;

	public RelationEntrySet() {
	}

	public RelationEntrySet(int initialCapacity) {
		super(initialCapacity);
	}

	public RelationEntrySet(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	protected RelationEntrySet<T> newInstance() {
		return new RelationEntrySet<>();
	}

	public boolean add(final Condition<T> match, final Condition<T> before, final Condition<T> after) {
		return add(new RelationEntry<T>(match, before, after));
	}

	/**
	 * Returns a deep copy of this <tt>RelationEntrySet</tt> instance: the elements
	 * themselves are copied.
	 *
	 * @return a deep copy of this set
	 */
	@Override
	public RelationEntrySet<T> clone() {
		final RelationEntrySet<T> set = newInstance();
		for (Iterator<RelationEntry<T>> i = this.iterator(); i.hasNext();) {
			final RelationEntry<T> entry = i.next();
			if (entry == null)
				set.add(null);
			else
				set.add(entry.copy());
		}
		return set;
	}

	public void compileTo(final ConditionIndexer indexer, final RelationEntrySet<T> set) {
		for (Iterator<RelationEntry<T>> i = this.iterator(); i.hasNext();) {
			final RelationEntry<T> entry = i.next();
			if (entry.validate(indexer))
				set.add(entry);
		}
	}

	public RelationEntrySet<T> compile(final ConditionIndexer indexer) {
		final RelationEntrySet<T> set = newInstance();
		compileTo(indexer, set);
		return set;
	}

	protected Checker checkerInstance() {
		return new Checker();
	}

	public void filterByApplicableData(final Collection<T> dataCollection) {
		final Checker checker = checkerInstance();

		for (Iterator<RelationEntry<T>> i = this.iterator(); i.hasNext();) {
			final RelationEntry<T> entry = i.next();
			if (entry == null || !checker.matchesAny(entry.getMatchItem(), dataCollection)) {
				i.remove();
				continue;
			}
			final boolean before = checker.matchesAny(entry.getMatchBefore(), dataCollection);
			final boolean after = checker.matchesAny(entry.getMatchAfter(), dataCollection);
			if (before || after) {
				if (!before)
					entry.setMatchBefore(null);
				if (!after)
					entry.setMatchAfter(null);
			} else
				i.remove();
		}
	}

	public void filterByApplicableDataTo(final Collection<T> dataCollection, final RelationEntrySet<T> set) {
		final Checker checker = checkerInstance();

		for (Iterator<RelationEntry<T>> i = this.iterator(); i.hasNext();) {
			final RelationEntry<T> entry = i.next();
			if (entry == null || !checker.matchesAny(entry.getMatchItem(), dataCollection))
				continue;
			final boolean before = checker.matchesAny(entry.getMatchBefore(), dataCollection);
			final boolean after = checker.matchesAny(entry.getMatchAfter(), dataCollection);
			if (before || after) {
				final RelationEntry<T> copy = entry.copy();
				if (!before)
					copy.setMatchBefore(null);
				if (!after)
					copy.setMatchAfter(null);
				set.add(copy);
			}
		}
	}
}
