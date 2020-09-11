package net.runeduniverse.libs.utils.async;

import java.util.concurrent.Semaphore;

public class Trigger extends AChain<Trigger> {
	private final Semaphore semaphore;

	public Trigger() {
		instance = this;
		this.semaphore = new Semaphore(0, true);
	}

	public Trigger(int permits) {
		instance = this;
		this.semaphore = new Semaphore(permits, true);
	}

	@Override
	public void execute() {
		try {
			semaphore.acquire();
		} catch (Exception e) {
			if (!handle(e))
				return;
		}
		done();
	}

	public void trigger() {
		this.semaphore.release();
	}
}
