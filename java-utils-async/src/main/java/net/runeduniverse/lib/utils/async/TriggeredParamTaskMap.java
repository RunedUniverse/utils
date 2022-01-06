/*
 * Copyright © 2022 Pl4yingNight (pl4yingnight@gmail.com)
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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class TriggeredParamTaskMap<KEY, PARAM> implements IRegistry<KEY>{
	private final Map<KEY, TriggeredParamTask<PARAM>> map = new HashMap<>();
	@Setter
	private Consumer<PARAM> consumer;

	public TriggeredParamTaskMap(Consumer<PARAM> consumer) {
		this.consumer = consumer;
	}

	@Override
	public TriggeredParamTask<PARAM> create(KEY key) {
		TriggeredParamTask<PARAM> task = this.map.get(key);
		if (task != null)
			return task;
		task = new TriggeredParamTask<PARAM>(this.consumer);
		this.map.put(key, task);
		return task;
	}

	public boolean containsKey(KEY key) {
		return this.map.containsKey(key);
	}

	public boolean containsTrigger(TriggeredParamTask<PARAM> task) {
		return this.map.containsValue(task);
	}

	public void remove(KEY key) {
		this.map.remove(key);
	}

	public void clear() {
		this.map.clear();
	}

	public void trigger(KEY key, PARAM param) {
		TriggeredParamTask<PARAM> task = this.map.get(key);
		if (task == null)
			return;
		task.trigger(param);
	}

	public void triggerAll(PARAM param) {
		for (KEY key : this.map.keySet())
			this.trigger(key, param);
	}
}
