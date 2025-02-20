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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.runeduniverse.lib.utils.conditions.api.Condition;

@NoArgsConstructor
@AllArgsConstructor
public class Entry<T> {
	@Getter
	protected Condition<T> matchItem = null;
	@Getter
	protected Condition<T> matchBefore = null;
	@Getter
	protected Condition<T> matchAfter = null;

	public Entry<T> setMatchItem(Condition<T> matchItem) {
		this.matchItem = matchItem;
		return this;
	}

	public Entry<T> setMatchBefore(Condition<T> matchBefore) {
		this.matchBefore = matchBefore;
		return this;
	}

	public Entry<T> setMatchAfter(Condition<T> matchAfter) {
		this.matchAfter = matchAfter;
		return this;
	}

	public boolean validate(final ConditionIndexer indexer) {
		if (this.matchItem == null)
			return false;
		this.matchItem.compile(true);
		if (indexer.detectCircle(this.matchItem) != null)
			return false;

		if (this.matchBefore != null) {
			this.matchBefore.compile(true);
			if (indexer.detectCircle(this.matchBefore) != null)
				return false;
		}

		if (this.matchAfter != null) {
			this.matchAfter.compile(true);
			if (indexer.detectCircle(this.matchAfter) != null)
				return false;
		}
		return true;
	}
}