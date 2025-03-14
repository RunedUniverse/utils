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
package net.runeduniverse.lib.utils.conditional.test.model;

import net.runeduniverse.lib.utils.conditional.ACondition;

public class MvnGoalCondition extends ACondition<MvnGoalView> {

	protected int priority = 0;

	protected String groupId;
	protected String artifactId;
	protected String goalId;

	public MvnGoalCondition(final String groupId, final String artifactId, final String goalId) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.goalId = goalId;
	}

	public MvnGoalCondition(final int priority, final String groupId, final String artifactId, final String goalId) {
		this(groupId, artifactId, goalId);
		this.priority = priority;
	}

	@Override
	public int getPriority() {
		return this.priority;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public String getArtifactId() {
		return this.artifactId;
	}

	public String getGoalId() {
		return this.goalId;
	}

	@Override
	protected DataCheck<MvnGoalView> check() {
		return and(nonNull(), this::eval);
	}

	protected boolean eval(final MvnGoalView view) {
		return getGroupId().equals(view.getGroupId()) && getArtifactId().equals(view.getArtifactId())
				&& getGoalId().equals(view.getGoalId());
	}
}
