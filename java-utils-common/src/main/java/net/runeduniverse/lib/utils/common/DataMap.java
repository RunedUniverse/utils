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
