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
package net.runeduniverse.lib.utils.chain;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Deprecated
// for internal use only
// depracted so that noone tries to get Store in chain args -> use ChainRuntime!
public final class Store {
	private final ChainRuntime<?> runtime;
	@Getter
	private final Map<Class<?>, Object> sourceDataMap = new HashMap<>();
	@Getter
	private final Map<Class<?>, Object> runtimeDataMap = new HashMap<>();
	private Object last = null;

	protected Store(ChainRuntime<?> runtime, Map<Class<?>, Object> sourceMap, Object[] args) {
		this.runtime = runtime;
		if (sourceMap != null)
			this.sourceDataMap.putAll(sourceMap);
		for (Object data : args)
			if (data != null)
				this.sourceDataMap.put(data.getClass(), data);
		this.runtimeDataMap.putAll(this.sourceDataMap);
	}

	public void putData(Class<?> dataType, Object data) {
		if (data == null) {
			if (dataType == null)
				return;
			this.runtimeDataMap.put(dataType, null);
		} else
			this.runtimeDataMap.put(data.getClass(), data);
		this.last = data;
	}

	@SuppressWarnings("unchecked")
	public <T> T getData(Class<T> type) {
		if (type == null)
			return null;
		if (type == ChainRuntime.class)
			return (T) this.runtime;
		Object obj = this.runtimeDataMap.get(type);
		if (obj != null)
			return (T) obj;

		for (Class<?> clazz : this.runtimeDataMap.keySet())
			if (type.isAssignableFrom(clazz))
				return (T) this.runtimeDataMap.get(clazz);
		return null;
	}

	public Object[] getData(Class<?>[] paramTypes) {
		Object[] arr = new Object[paramTypes.length];
		for (int i = 0; i < paramTypes.length; i++)
			arr[i] = this.getData(paramTypes[i]);
		return arr;
	}

	public Object getLastAdded() {
		return this.last;
	}
}