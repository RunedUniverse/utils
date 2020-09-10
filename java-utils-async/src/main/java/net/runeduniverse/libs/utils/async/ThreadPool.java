package net.runeduniverse.libs.utils.async;

import java.util.ArrayList;
import java.util.Collection;

public class ThreadPool {

	private boolean bury = true;
	private boolean autostart = true;
	private Collection<Thread> pool = new ArrayList<>();

	// SETTINGS
	public ThreadPool burying(boolean bury) {
		this.bury = bury;
		return this;
	}

	public ThreadPool autostart(boolean start) {
		this.autostart = start;
		return this;
	}

	// METHODS
	public ThreadPool add(Thread thread) {
		if (autostart)
			thread.start();
		this.pool.add(thread);
		return this;
	}

	public ThreadPool add(Runnable runnable) {
		return this.add(new Thread(runnable));
	}

	public ThreadPool join() {
		for (Thread thread : pool)
			try {
				thread.join();
			} catch (InterruptedException e) {
				if (!bury)
					e.printStackTrace();
			}
		return this;
	}

	public ThreadPool interrupt() {
		for (Thread thread : pool)
			thread.interrupt();
		return this;
	}

}
