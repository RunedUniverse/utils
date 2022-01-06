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
package net.runeduniverse.lib.utils.common;

import java.util.Set;
import java.util.function.BiConsumer;

public interface DataMap<K, V, D> {
	V put(K key, V value);

	V put(K key, V value, D data);

	V get(K key);

	void setData(K key, D data);

	D getData(K key);

	int size();

	boolean containsKey(K key);

	boolean containsKey(K key, D data);

	boolean containsValue(V value);

	boolean containsValue(V value, D data);

	void forEach(BiConsumer<K, V> action);

	void forEach(D data, BiConsumer<K, V> action);

	void forEach(TriConsumer<K, V, D> action);

	Set<K> keySet();

	Set<Value<V, D>> valueSet();

	public interface Value<V, M> {
		public V getValue();

		public M getData();
	}
}
