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
import java.util.Iterator;
import net.runeduniverse.lib.utils.conditions.api.Condition;

public class OrCondition<T> extends AConditionGroup<T> {

	public OrCondition() {
		super();
	}

	public OrCondition(final Collection<Condition<T>> conditions) {
		super(conditions);
	}

	@Override
	public String getType() {
		return "or";
	}

	@Override
	public boolean evaluate(T entity) {
		for (Iterator<Condition<T>> i = this.conditions.iterator(); i.hasNext();) {
			if (i.next()
					.evaluate(entity))
				return true;
		}
		return false;
	}
}
