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
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import lombok.Data;
import net.runeduniverse.lib.utils.common.api.DataMap;
import net.runeduniverse.lib.utils.common.api.TriConsumer;

public abstract class AbstractDataMap<K, V, D> implements DataMap<K, V, D> {

	protected final Map<K, MEntry<K, V, D>> map;

	protected AbstractDataMap(final Map<K, MEntry<K, V, D>> map) {
		this.map = map;
	}

	@Override
	public V put(final K key, final V value) {
		final MEntry<K, V, D> entry = createEntry(key);
		entry.setValue(value);
		return MEntry.getValue(this.map.put(key, entry));
	}

	@Override
	public V put(final K key, final V value, final D data) {
		final MEntry<K, V, D> entry = createEntry(key);
		entry.setValue(value);
		entry.setData(data);
		return MEntry.getValue(this.map.put(key, entry));
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
	public V putValue(final K key, final V value) {
		final MEntry<K, V, D> entry = this.map.computeIfAbsent(key, this::createEntry);
		final V oldValue = entry.getValue();
		entry.setValue(value);
		return oldValue;
	}

	@Override
	public D putData(final K key, final D data) {
		final MEntry<K, V, D> entry = this.map.computeIfAbsent(key, this::createEntry);
		final D oldData = entry.getData();
		entry.setData(data);
		return oldData;
	}

	@Override
	public void setValue(K key, V value) {
		final MEntry<K, V, D> entry = this.map.get(key);
		if (entry != null)
			entry.setValue(value);
	}

	@Override
	public void setData(final K key, final D data) {
		final MEntry<K, V, D> entry = this.map.get(key);
		if (entry != null)
			entry.setData(data);
	}

	@Override
	public D getData(final K key) {
		return MEntry.getData(this.map.get(key));
	}

	@Override
	public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
		final MEntry<K, V, D> entry = this.map.computeIfAbsent(key, this::createEntry);
		return entry.computeValueIfAbsent(mappingFunction);
	}

	@Override
	public D computeDataIfAbsent(final K key, final Function<? super K, ? extends D> mappingFunction) {
		final MEntry<K, V, D> entry = this.map.computeIfAbsent(key, this::createEntry);
		return entry.computeDataIfAbsent(mappingFunction);
	}

	@Override
	public V computeIfPresent(final K key, BiFunction<? super K, ? super V, ? extends V> mappingFunction) {
		final MEntry<K, V, D> newEntry = this.map.computeIfPresent(key, (k, entry) -> {
			if (entry.getValue() == null)
				return entry;
			if (entry.computeValueIfPresent(mappingFunction) == null)
				return null;
			return entry;
		});
		if (newEntry == null)
			return null;
		return newEntry.getValue();
	}

	@Override
	public D computeDataIfPresent(final K key, BiFunction<? super K, ? super D, ? extends D> mappingFunction) {
		final MEntry<K, V, D> newEntry = this.map.computeIfPresent(key, (k, entry) -> {
			entry.computeDataIfPresent(mappingFunction);
			return entry;
		});
		if (newEntry == null)
			return null;
		return newEntry.getData();
	}

	@Override
	public int size() {
		return this.map.size();
	}

	@Override
	public boolean isEmpty() {
		return this.map.isEmpty();
	}

	@Override
	public boolean containsKey(final K key) {
		return this.map.containsKey(key);
	}

	@Override
	public boolean containsKey(final K key, final D data) {
		final MEntry<K, V, D> entry = this.map.get(key);
		if (entry == null)
			return false;
		final D eData = entry.getData();
		if (eData == null)
			return false;
		return eData.equals(data);
	}

	@Override
	public boolean containsValue(final V value) {
		for (MEntry<K, V, D> entry : this.map.values()) {
			final V eValue = entry.getValue();
			if (eValue != null && eValue.equals(value))
				return true;
		}
		return false;
	}

	@Override
	public boolean containsValue(final V value, final D data) {
		for (MEntry<K, V, D> entry : this.map.values()) {
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
		for (Map.Entry<K, MEntry<K, V, D>> entry : this.map.entrySet())
			action.accept(entry.getKey(), MEntry.getValue(entry.getValue()));
	}

	@Override
	public void forEach(final D modifier, final BiConsumer<K, V> action) {
		for (Map.Entry<K, MEntry<K, V, D>> entry : this.map.entrySet()) {
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
		for (Map.Entry<K, MEntry<K, V, D>> entry : this.map.entrySet())
			action.accept(entry.getKey(), MEntry.getValue(entry.getValue()), MEntry.getData(entry.getValue()));
	}

	@Override
	public Set<K> keySet() {
		return this.map.keySet();
	}

	@Override
	public Set<DataMap.Entry<K, V, D>> entrySet() {
		return new HashSet<DataMap.Entry<K, V, D>>(this.map.values());
	}

	protected MEntry<K, V, D> createEntry(final K key) {
		return new MEntry<>(key);
	}

	protected Map<K, V> createValueMap() {
		return new HashMap<>();
	}

	protected Map<K, D> createDataMap() {
		return new HashMap<>();
	}

	@Override
	public Map<K, V> toValueMap() {
		final Map<K, V> map = createValueMap();

		for (Map.Entry<K, MEntry<K, V, D>> entry : this.map.entrySet())
			map.put(entry.getKey(), MEntry.getValue(entry.getValue()));

		return map;
	}

	@Override
	public Map<K, D> toDataMap() {
		final Map<K, D> map = createDataMap();

		for (Map.Entry<K, MEntry<K, V, D>> entry : this.map.entrySet())
			map.put(entry.getKey(), MEntry.getData(entry.getValue()));

		return map;
	}

	@Data
	protected static class MEntry<K, V, D> implements DataMap.Entry<K, V, D> {

		protected final K key;
		protected V value = null;
		protected D data = null;

		protected MEntry(final K key) {
			this.key = key;
		}

		public Object lock() {
			return this;
		}

		public void setValue(final V value) {
			synchronized (lock()) {
				this.value = value;
			}
		}

		public void setData(final D data) {
			synchronized (lock()) {
				this.data = data;
			}
		}

		@Override
		public V computeValueIfAbsent(final Function<? super K, ? extends V> mappingFunction) {
			synchronized (lock()) {
				return Entry.super.computeValueIfAbsent(mappingFunction);
			}
		}

		@Override
		public D computeDataIfAbsent(final Function<? super K, ? extends D> mappingFunction) {
			synchronized (lock()) {
				return Entry.super.computeDataIfAbsent(mappingFunction);
			}
		}

		@Override
		public V computeValueIfPresent(final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
			synchronized (lock()) {
				return Entry.super.computeValueIfPresent(remappingFunction);
			}
		}

		@Override
		public D computeDataIfPresent(final BiFunction<? super K, ? super D, ? extends D> remappingFunction) {
			synchronized (lock()) {
				return Entry.super.computeDataIfPresent(remappingFunction);
			}
		}

		public static <K, V, D> V getValue(final MEntry<K, V, D> entry) {
			return entry == null ? null : entry.getValue();
		}

		public static <K, V, D> D getData(final MEntry<K, V, D> entry) {
			return entry == null ? null : entry.getData();
		}

	}

}
