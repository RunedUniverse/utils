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

import java.util.Iterator;
import java.util.LinkedHashSet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.runeduniverse.lib.utils.conditions.api.Condition;

public class EntrySet<T> extends LinkedHashSet<EntrySet.Entry<T>> {

	private static final long serialVersionUID = 1L;

	public EntrySet() {
	}

	public EntrySet(int initialCapacity) {
		super(initialCapacity);
	}

	public EntrySet(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public EntrySet<T> compile(final ConditionIndexer indexer) {
		final EntrySet<T> set = new EntrySet<>();
		for (Iterator<Entry<T>> i = this.iterator(); i.hasNext();) {
			final Entry<T> entry = i.next();
			if (entry.validate(indexer))
				set.add(entry);
		}
		return set;
	}

	public boolean add(final Condition<T> match, final Condition<T> before, final Condition<T> after) {
		return add(new Entry<T>(match, before, after));
	}

	@NoArgsConstructor
	@AllArgsConstructor
	public static class Entry<T> {
		@Getter
		@Setter
		protected Condition<T> match = null;
		@Getter
		@Setter
		protected Condition<T> before = null;
		@Getter
		@Setter
		protected Condition<T> after = null;

		public boolean validate(ConditionIndexer indexer) {
			if (this.match == null)
				return false;
			this.match.compile(true);
			if (indexer.detectCircle(this.match) != null)
				return false;

			if (this.before != null) {
				this.before.compile(true);
				if (indexer.detectCircle(this.before) != null)
					return false;
			}

			if (this.after != null) {
				this.after.compile(true);
				if (indexer.detectCircle(this.after) != null)
					return false;
			}
			return true;
		}
	}
}
