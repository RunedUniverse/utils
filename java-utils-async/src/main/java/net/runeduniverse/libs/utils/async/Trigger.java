package net.runeduniverse.libs.utils.async;

import java.util.concurrent.Semaphore;

public class Trigger extends AChain<Trigger> {
	private Semaphore semaphore = new Semaphore(0);

	public Trigger() {
		instance = this;
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
