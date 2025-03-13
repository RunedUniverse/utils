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
package net.runeduniverse.lib.utils.conditional;

import net.runeduniverse.lib.utils.conditional.api.Condition;
import net.runeduniverse.lib.utils.conditional.api.ConditionModifier;

public abstract class AConditionModifier<T> extends ACondition<T> implements ConditionModifier<T> {

	protected Condition<T> condition = null;

	@Override
	protected DataCheck<T> check() {
		return d -> false;
	}

	@Override
	public Condition<T> getCondition() {
		return this.condition;
	}

	@Override
	public boolean setCondition(final Condition<T> condition) {
		this.condition = condition;
		return true;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!(super.equals(obj) && obj instanceof ConditionModifier<?>))
			return false;

		final ConditionModifier<?> other = (ConditionModifier<?>) obj;
		final Condition<?> conA = getCondition(), conB = other.getCondition();
		if (conA == conB)
			return true;
		if (conA == null || conB == null)
			return false;
		return conA.equals(conB);
	}
}
