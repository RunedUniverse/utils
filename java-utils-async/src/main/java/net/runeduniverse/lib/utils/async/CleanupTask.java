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
package net.runeduniverse.lib.utils.async;

public class CleanupTask extends AChain<CleanupTask> {

	private Runnable task = null;

	public CleanupTask() {
		this(null);
	}

	public CleanupTask(Runnable task) {
		instance = this;
		this.ignoreStop = true;
		this.task = task;
	}

	public CleanupTask setTask(Runnable task) {
		this.task = task;
		return this;
	}

	@Override
	public void execute() {
		if (this.task != null)
			this.task.run();
		done();
	}
}
