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
package net.runeduniverse.lib.utils.conditions;

import java.util.Collection;
import java.util.LinkedList;
import net.runeduniverse.lib.utils.conditions.api.Condition;
import net.runeduniverse.lib.utils.conditions.api.ConditionGroup;

public abstract class AConditionGroup<T> extends ACondition<T> implements ConditionGroup<T> {

	protected final Collection<Condition<T>> conditions;

	public AConditionGroup() {
		this.conditions = new LinkedList<>();
	}

	public AConditionGroup(final Collection<Condition<T>> conditions) {
		this.conditions = conditions;
	}

	@Override
	public Collection<Condition<T>> getGroup() {
		return this.conditions;
	}

	@Override
	public boolean add(Condition<T> condition) {
		return this.conditions.add(condition);
	}

	@Override
	public boolean remove(Condition<T> condition) {
		return this.conditions.remove(condition);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(super.equals(obj) && obj instanceof ConditionGroup<?>))
			return false;

		final ConditionGroup<?> grp = (ConditionGroup<?>) obj;
		final Collection<?> colA = this.getGroup(), colB = grp.getGroup();
		if (colA == null) {
			if (colB == null)
				return true;
			return false;
		} else if (colB == null)
			return false;
		return colA.equals(colB);
	}

	@Override
	public boolean clear() {
		try {
			this.conditions.clear();
			return true;
		} catch (UnsupportedOperationException e) {
			return false;
		}
	}
}
