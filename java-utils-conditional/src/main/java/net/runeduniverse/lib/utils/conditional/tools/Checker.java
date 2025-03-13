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
package net.runeduniverse.lib.utils.conditional.tools;

import java.util.Collection;

import net.runeduniverse.lib.utils.conditional.api.Condition;

public class Checker {

	public <T> boolean matches(final Condition<T> condition, final T entity) {
		if (condition == null)
			return false;
		return condition.evaluate(entity);
	}

	public <T> boolean matchesAny(final Condition<T> condition, final Collection<T> entityCollection) {
		if (condition == null)
			return false;
		for (T entity : entityCollection) {
			if (matches(condition, entity))
				return true;
		}
		return false;
	}
}
