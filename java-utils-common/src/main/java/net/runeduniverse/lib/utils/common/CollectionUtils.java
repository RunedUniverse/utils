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
package net.runeduniverse.lib.utils.common;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.function.Supplier;

public class CollectionUtils {

	public static final <T> T first(final Collection<T> collection) {
		for (T t : collection)
			return t;
		return null;
	}

	public static final <T> T firstNotNull(final Collection<T> collection) {
		for (T t : collection)
			if (t != null)
				return t;
		return null;
	}

	public static final <T> T last(final Collection<T> collection) {
		if (collection instanceof Deque) {
			final Deque<T> deque = (Deque<T>) collection;
			return deque.getLast();
		}
		for (Iterator<T> i = collection.iterator(); i.hasNext();) {
			boolean next = i.hasNext();
			final T obj = (T) i.next();
			if (!next)
				return obj;
		}
		return null;
	}

	public static final <T> T lastNotNull(final Collection<T> collection) {
		// check for deque as it can be efficiently interated backwards
		if (collection instanceof Deque) {
			final Deque<T> deque = (Deque<T>) collection;
			for (Iterator<T> i = deque.descendingIterator(); i.hasNext();) {
				final T obj = (T) i.next();
				if (obj != null)
					return obj;
			}
			return null;
		}
		// otherwise search everything
		T value = null;
		for (Iterator<T> i = collection.iterator(); i.hasNext();) {
			boolean next = i.hasNext();
			final T obj = (T) i.next();
			if (obj != null)
				value = obj;
			if (!next)
				return obj;
		}
		return value;
	}

	public static final <T> List<T> copy(final List<T> src, final Supplier<List<T>> supplier) {
		final List<T> lst = supplier.get();
		lst.addAll(src);
		return lst;
	}

	public static final <T> Set<T> copy(final Set<T> src, final Supplier<Set<T>> supplier) {
		final Set<T> set = supplier.get();
		set.addAll(src);
		return set;
	}

	public static final <K, V> Map<K, V> copy(final Map<K, V> src, final Supplier<Map<K, V>> supplier) {
		final Map<K, V> map = supplier.get();
		for (Entry<K, V> entry : src.entrySet()) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}

	public static final <T> List<T> unmodifiable(final List<T> src) {
		return Collections.unmodifiableList(src);
	}

	public static final <T> Set<T> unmodifiable(final Set<T> src) {
		if (src instanceof NavigableSet)
			return Collections.unmodifiableNavigableSet((NavigableSet<T>) src);
		if (src instanceof SortedSet)
			return Collections.unmodifiableSortedSet((SortedSet<T>) src);
		return Collections.unmodifiableSet(src);
	}

	public static final <K, V> Map<K, V> unmodifiable(final Map<K, V> src) {
		if (src instanceof NavigableMap)
			return Collections.unmodifiableNavigableMap((NavigableMap<K, V>) src);
		else if (src instanceof SortedMap)
			return Collections.unmodifiableSortedMap((SortedMap<K, V>) src);
		return Collections.unmodifiableMap(src);
	}
}
