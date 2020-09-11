package net.runeduniverse.libs.utils.async;

import java.util.HashMap;
import java.util.Map;

public class TriggerMap {
	private Map<Object, Trigger> map = new HashMap<>();

	public Trigger create(Object key) {
		Trigger trigger = new Trigger();
		this.map.put(key, trigger);
		return trigger;
	}

	public void trigger(Object key) {
		Trigger trigger = this.map.get(key);
		if (trigger == null)
			return;
		trigger.trigger();
	}
}
