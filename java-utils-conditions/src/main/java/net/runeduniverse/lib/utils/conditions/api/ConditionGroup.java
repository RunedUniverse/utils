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

import java.util.Iterator;
import java.util.List;

public interface ConditionGroup<T> extends Condition<T> {

	public List<Condition<T>> getGroup();

	public boolean add(Condition<T> condition);

	public boolean remove(Condition<T> condition);

	public boolean clear();

	@Override
	default boolean isValid() {
		final List<Condition<T>> group = getGroup();
		return group != null && !group.isEmpty();
	}

	// Compiling the condition removes all underlying invalid conditions.
	@Override
	default void compile(boolean recurse) {
		for (Iterator<Condition<T>> i = getGroup().iterator(); i.hasNext();) {
			final Condition<T> con = i.next();
			if (con == null) {
				i.remove();
				continue;
			}
			if (recurse)
				con.compile(true);
			if (!con.isValid())
				i.remove();
		}
	}

}
