package net.runeduniverse.lib.utils.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;

import lombok.Data;

public class DataHashMap<K, V, D> implements DataMap<K, V, D> {

	private Map<K, MEntry<V, D>> map = new HashMap<>();

	@Override
	public V put(K key, V value) {
		return MEntry.getValue(this.map.put(key, new MEntry<>(value)));
	}

	@Override
	public V put(K key, V value, D data) {
		return MEntry.getValue(this.map.put(key, new MEntry<>(value, data)));
	}

	@Override
	public V get(K key) {
		return MEntry.getValue(this.map.get(key));
	}

	@Override
	public void setData(K key, D data) {
		this.map.get(key)
				.setData(data);
	}

	@Override
	public D getData(K key) {
		return MEntry.getData(this.map.get(key));
	}

	@Override
	public int size() {
		return this.map.size();
	}

	@Override
	public boolean containsKey(K key) {
		return this.map.containsKey(key);
	}

	@Override
	public boolean containsKey(K key, D data) {
		MEntry<V, D> entry = this.map.get(key);
		if (entry == null || entry.getData() == null)
			return false;
		return entry.getData()
				.equals(data);
	}

	@Override
	public boolean containsValue(V value) {
		for (MEntry<V, D> me : this.map.values())
			if (me.getValue()
					.equals(value))
				return true;
		return false;
	}

	@Override
	public boolean containsValue(V value, D data) {
		for (MEntry<V, D> me : this.map.values())
			if (me.getData() != null && me.getData()
					.equals(data) && me.getValue()
							.equals(value))
				return true;
		return false;
	}

	@Override
	public void forEach(BiConsumer<K, V> action) {
		for (Entry<K, MEntry<V, D>> entry : this.map.entrySet())
			action.accept(entry.getKey(), MEntry.getValue(entry.getValue()));
	}

	@Override
	public void forEach(D modifier, BiConsumer<K, V> action) {
		for (Entry<K, MEntry<V, D>> entry : this.map.entrySet())
			if (MEntry.getData(entry.getValue())
					.equals(modifier))
				action.accept(entry.getKey(), MEntry.getValue(entry.getValue()));
	}

	@Override
	public void forEach(TriConsumer<K, V, D> action) {
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
