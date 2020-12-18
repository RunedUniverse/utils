package net.runeduniverse.libs.utils.async;

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
