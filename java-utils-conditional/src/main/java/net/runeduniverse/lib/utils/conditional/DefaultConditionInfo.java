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
package net.runeduniverse.lib.utils.conditional;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.runeduniverse.lib.utils.conditional.api.Condition;

@NoArgsConstructor
@AllArgsConstructor
public class DefaultConditionInfo implements Condition.ConditionInfo {

	@Setter
	@Accessors(chain = true)
	protected String tag;
	@Setter
	@Accessors(chain = true)
	protected String text;

	@Override
	public CharSequence getTag() {
		return this.tag;
	}

	@Override
	public CharSequence getText() {
		return this.text;
	}
}
