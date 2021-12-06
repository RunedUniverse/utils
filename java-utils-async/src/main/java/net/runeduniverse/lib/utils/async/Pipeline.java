package net.runeduniverse.lib.utils.async;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pipeline<KEY> {

	private final int concurrentPipes;
	private final TriggerMap<KEY> resetMap;
	private final List<IRegistry<KEY>> pipeRegistry = new ArrayList<IRegistry<KEY>>();
	private final Map<KEY, List<IChainable<?>>> activeChains = new HashMap<>();

	public Pipeline() {
		this(1);
	}

	public Pipeline(int concurrentPipes) {
		this.concurrentPipes = concurrentPipes;
		this.resetMap = new TriggerMap<KEY>(this.concurrentPipes);
	}

	public Pipeline<KEY> append(TriggerMap<KEY> map) {
		this.pipeRegistry.add(map);
		return this;
	}

	public Pipeline<KEY> append(TriggeredParamTaskMap<KEY, ?> map) {
		this.pipeRegistry.add(map);
		return this;
	}

	public IChainable<?> build(KEY key) {
		IChainable<?> last = this.resetMap.create(key);
		for (IRegistry<KEY> r : pipeRegistry)
			last = last.append(r.create(key));

		CleanupTask t = new CleanupTask();
		final IChainable<?> chain = last.append(t);
		t.setTask(new Runnable() {

			@Override
			public void run() {
				Pipeline.this.resetMap.trigger(key);
				Pipeline.this.removeChain(key, chain);
			}
		});

		return this.addChain(key, chain);
	}

	public void stopFirst(KEY key) {
		List<IChainable<?>> l = this.activeChains.get(key);
		if (l == null)
			return;
		for (IChainable<?> chain : l) {
			chain.stop();
			return;
		}
	}

	public void stopAll(KEY key) {
		List<IChainable<?>> l = this.activeChains.get(key);
		if (l == null)
			return;
		for (IChainable<?> chain : l)
			chain.stop();
	}

	public void clear() {
		for (KEY key : this.activeChains.keySet())
			this.stopAll(key);

		this.resetMap.clear();
		this.activeChains.clear();
	}

	private IChainable<?> addChain(KEY key, IChainable<?> chain) {
		List<IChainable<?>> l = this.activeChains.get(key);
		if (l == null) {
			l = new ArrayList<>();
			this.activeChains.put(key, l);
		}
		l.add(chain);
		return chain;
	}

	private void removeChain(KEY key, IChainable<?> chain) {
		List<IChainable<?>> l = this.activeChains.get(key);
		if (l == null)
			return;
		l.remove(chain);
	}
}
