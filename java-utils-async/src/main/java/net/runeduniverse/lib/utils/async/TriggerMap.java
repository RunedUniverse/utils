/*
 * Copyright © 2021 Pl4yingNight (pl4yingnight@gmail.com)
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

public class TriggerMap<KEY> implements IRegistry<KEY> {

	private final int initPermits;
	private final Map<KEY, Trigger> map = new HashMap<>();

	public TriggerMap() {
		this.initPermits = 0;
	}

	public TriggerMap(int initPermits) {
		this.initPermits = initPermits;
	}

	@Override
	public Trigger create(KEY key) {
		Trigger trigger = this.map.get(key);
		if (trigger != null)
			return trigger;
		trigger = new Trigger(this.initPermits);
		this.map.put(key, trigger);
		return trigger;
	}

	public boolean containsKey(KEY key) {
		return this.map.containsKey(key);
	}

	public boolean containsTrigger(Trigger trigger) {
		return this.map.containsValue(trigger);
	}

	public void remove(KEY key) {
		this.map.remove(key);
	}

	public void clear() {
		this.map.clear();
	}

	public void trigger(KEY key) {
		Trigger trigger = this.map.get(key);
		if (trigger == null)
			return;
		trigger.trigger();
	}

	public void triggerAll() {
		for (KEY key : this.map.keySet())
			this.trigger(key);
	}
}
