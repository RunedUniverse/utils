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
package net.runeduniverse.lib.utils.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;

import lombok.Data;
import net.runeduniverse.lib.utils.common.api.DataMap;
import net.runeduniverse.lib.utils.common.api.TriConsumer;

public class DataHashMap<K, V, D> implements DataMap<K, V, D> {

	protected final Map<K, MEntry<V, D>> map;

	public DataHashMap() {
		this.map = new HashMap<>();
	}

	protected DataHashMap(final Map<K, MEntry<V, D>> map) {
		this.map = map;
	}

	@Override
	public V put(final K key, final V value) {
		return MEntry.getValue(this.map.put(key, new MEntry<>(value)));
	}

	@Override
	public V put(final K key, final V value, final D data) {
		return MEntry.getValue(this.map.put(key, new MEntry<>(value, data)));
	}

	@Override
	public V get(final K key) {
		return MEntry.getValue(this.map.get(key));
	}

	@Override
	public V remove(final K key) {
		return MEntry.getValue(this.map.remove(key));
	}

	@Override
	public void clear() {
		this.map.clear();
	}

	@Override
	public void setData(final K key, final D data) {
		final MEntry<V, D> entry = this.map.get(key);
		if (entry != null)
			entry.setData(data);
	}

	@Override
	public D getData(final K key) {
		return MEntry.getData(this.map.get(key));
	}

	@Override
	public int size() {
		return this.map.size();
	}

	@Override
	public boolean containsKey(final K key) {
		return this.map.containsKey(key);
	}

	@Override
	public boolean containsKey(final K key, final D data) {
		final MEntry<V, D> entry = this.map.get(key);
		if (entry == null)
			return false;
		final D eData = entry.getData();
		if (eData == null)
			return false;
		return eData.equals(data);
	}

	@Override
	public boolean containsValue(final V value) {
		for (MEntry<V, D> entry : this.map.values()) {
			final V eValue = entry.getValue();
			if (eValue != null && eValue.equals(value))
				return true;
		}
		return false;
	}

	@Override
	public boolean containsValue(final V value, final D data) {
		for (MEntry<V, D> entry : this.map.values()) {
			final D eData = entry.getData();
			if (eData != null && eData.equals(data)) {
				final V meValue = entry.getValue();
				if (meValue != null && meValue.equals(value))
					return true;
			}
		}
		return false;
	}

	@Override
	public void forEach(final BiConsumer<K, V> action) {
		for (Entry<K, MEntry<V, D>> entry : this.map.entrySet())
			action.accept(entry.getKey(), MEntry.getValue(entry.getValue()));
	}

	@Override
	public void forEach(final D modifier, final BiConsumer<K, V> action) {
		for (Entry<K, MEntry<V, D>> entry : this.map.entrySet()) {
			final D eData = MEntry.getData(entry.getValue());
			if (eData == null) {
				if (modifier == null)
					action.accept(entry.getKey(), MEntry.getValue(entry.getValue()));
			} else if (eData.equals(modifier))
				action.accept(entry.getKey(), MEntry.getValue(entry.getValue()));
		}
	}

	@Override
	public void forEach(final TriConsumer<K, V, D> action) {
		for (Entry<K, MEntry<V, D>> entry : this.map.entrySet())
			action.accept(entry.getKey(), MEntry.getValue(entry.getValue()), MEntry.getData(entry.getValue()));
	}

	@Override
	public Set<K> keySet() {
		return this.map.keySet();
	}

	@Override
	public Set<DataMap.Value<V, D>> valueSet() {
		return new HashSet<DataMap.Value<V, D>>(this.map.values());
	}

	@Data
	protected static class MEntry<V, D> implements DataMap.Value<V, D> {

		private V value;
		private D data;

		protected MEntry(V value) {
			this.value = value;
			this.data = null;
		}

		protected MEntry(V value, D data) {
			this.value = value;
			this.data = data;
		}

		public static <V, D> V getValue(MEntry<V, D> entry) {
			return entry == null ? null : entry.getValue();
		}

		public static <V, D> D getData(MEntry<V, D> entry) {
			return entry == null ? null : entry.getData();
		}

	}

}
