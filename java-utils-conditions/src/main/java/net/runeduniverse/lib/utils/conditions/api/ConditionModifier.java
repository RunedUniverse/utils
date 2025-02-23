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
package net.runeduniverse.lib.utils.conditions.api;

public interface ConditionModifier<T> extends Condition<T> {

	public Condition<T> getCondition();

	public boolean setCondition(final Condition<T> condition);

	@Override
	default boolean isValid() {
		return getCondition() != null;
	}

	@Override
	default void compile(final boolean recurse) {
		final Condition<T> con = getCondition();
		if (con == null)
			return;
		if (recurse)
			con.compile(true);
		if (!con.isValid())
			setCondition(null);
	}
}
