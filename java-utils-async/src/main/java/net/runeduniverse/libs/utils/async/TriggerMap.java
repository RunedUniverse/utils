package net.runeduniverse.libs.utils.async;

import java.util.HashMap;
import java.util.Map;

public class TriggerMap<KEY> {
	private Map<KEY, Trigger> map = new HashMap<>();

	public Trigger create(KEY key) {
		Trigger trigger = this.map.get(key);
		if (trigger != null)
			return trigger;
		trigger = new Trigger();
		this.map.put(key, trigger);
		return trigger;
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
