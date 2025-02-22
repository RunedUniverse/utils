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
package net.runeduniverse.lib.utils.conditions.test.model;

public class ModelFactory {

	public static MvnGoalCondition createGoalConditionRevelcFormatter() {
		return new MvnGoalCondition("net.revelc.code.formatter", "formatter-maven-plugin", "format");
	}

	public static MvnGoalCondition createGoalConditionRevelcFormatter(final int priority) {
		return new MvnGoalCondition(priority, "net.revelc.code.formatter", "formatter-maven-plugin", "format");
	}

	public static MvnGoalView createGoalViewRevelcFormatter() {
		return new MvnGoalView("net.revelc.code.formatter", "formatter-maven-plugin", "format");
	}

	public static MvnGoalCondition createGoalConditionMycilaFormatter() {
		return new MvnGoalCondition("com.mycila", "license-maven-plugin", "format");
	}

	public static MvnGoalCondition createGoalConditionMycilaFormatter(final int priority) {
		return new MvnGoalCondition(priority, "com.mycila", "license-maven-plugin", "format");
	}

	public static MvnGoalView createGoalViewMycilaFormatter() {
		return new MvnGoalView("com.mycila", "license-maven-plugin", "format");
	}
}
