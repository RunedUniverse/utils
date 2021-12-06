package net.runeduniverse.lib.utils.async;

import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

public class TriggeredParamTask<PARAM> extends AChain<TriggeredParamTask<PARAM>> {

	private final Semaphore semaphore;
	private Consumer<PARAM> consumer = null;
	private PARAM param = null;

	public TriggeredParamTask() {
		instance = this;
		this.semaphore = new Semaphore(0, true);
	}

	public TriggeredParamTask(Consumer<PARAM> consumer) {
		instance = this;
		this.semaphore = new Semaphore(0, true);
		this.consumer = consumer;
	}

	public TriggeredParamTask<PARAM> setConsumer(Consumer<PARAM> consumer) {
		this.consumer = consumer;
		return this;
	}

	@Override
	public void execute() {
		try {
			semaphore.acquire();
		} catch (Exception e) {
			if (!handle(e))
				return;
		}
		if (this.consumer != null)
			this.consumer.accept(this.param);
		done();
	}

	public void trigger(PARAM param) {
		this.param = param;
		this.semaphore.release();
	}
}
