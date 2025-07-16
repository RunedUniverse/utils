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
package net.runeduniverse.lib.utils.common.api;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface DataMap<K, V, D> {

	public V put(K key, V value);

	public V put(K key, V value, D data);

	public V get(K key);

	public D getData(K key);

	public V remove(K key);

	public void clear();

	public V putValue(K key, V value);

	public D putData(K key, D data);

	public void setValue(K key, V value);

	public void setData(K key, D data);

	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction);

	public D computeDataIfAbsent(K key, Function<? super K, ? extends D> mappingFunction);

	public int size();

	public boolean isEmpty();

	public boolean containsKey(K key);

	public boolean containsKey(K key, D data);

	public boolean containsValue(V value);

	public boolean containsValue(V value, D data);

	public void forEach(BiConsumer<K, V> action);

	public void forEach(D data, BiConsumer<K, V> action);

	public void forEach(TriConsumer<K, V, D> action);

	public Set<K> keySet();

	public Set<InternalEntry<K, V, D>> internalEntrySet();

	public Map<K, V> toValueMap();

	public Map<K, D> toDataMap();

	public interface InternalEntry<K, V, D> {

		public Object lock();

		public K getKey();

		public V getValue();

		public D getData();

		public void setValue(V value);

		public void setData(D data);

		public default <T> V computeValueIfAbsent(final T key, final Function<? super T, ? extends V> mappingFunction) {
			synchronized (lock()) {
				V value = getValue();
				if (value != null)
					return value;
				setValue(value = mappingFunction.apply(key));
				return value;
			}
		}

		public default <T> D computeDataIfAbsent(final T key, final Function<? super T, ? extends D> mappingFunction) {
			synchronized (lock()) {
				D data = getData();
				if (data != null)
					return data;
				setData(data = mappingFunction.apply(key));
				return data;
			}
		}

	}

}
