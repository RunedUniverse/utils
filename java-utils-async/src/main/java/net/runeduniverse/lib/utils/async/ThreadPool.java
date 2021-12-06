package net.runeduniverse.lib.utils.async;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;

import lombok.RequiredArgsConstructor;

public class ThreadPool extends AChain<ThreadPool> {

	private final Collection<Thread> pool = new ArrayList<>();
	private CountDownLatch latch;

	public ThreadPool() {
		instance = this;
	}

	public ThreadPool add(Thread thread) {
		switch (thread.getState()) {
		case TERMINATED:
			return this;

		case NEW:
			thread = new Thread(new CDLRunnable(this, thread));
		default:
			this.pool.add(thread);
			return this;
		}
	}

	public ThreadPool add(Runnable runnable) {
		this.pool.add(new Thread(new CDLRunnable(this, runnable)));
		return this;
	}

	public ThreadPool start() {
		this.latch = new CountDownLatch(this.pool.size());
		for (Thread thread : this.pool) {
			thread.start();
			this.latch.countDown();
		}
		return this;
	}

	public ThreadPool join() {
		for (Thread thread : this.pool)
			try {
				switch (thread.getState()) {
				case TERMINATED:
				case NEW:
					break;

				default:
					thread.join();
				}
			} catch (Exception e) {
				if (!handle(e))
					return this;
			}
		return this;
	}

	public ThreadPool interrupt() {
		for (Thread thread : this.pool)
			thread.interrupt();
		return this;
	}

	@Override
	public void execute() {
		this.start();
		this.join();
		done();
	}

	public void clear() {
		this.pool.clear();
	}

	// only for CDLRunnable
	private void await() {
		if (this.latch == null)
			return;
		try {
			this.latch.await();
		} catch (Exception e) {
			if (!handle(e))
				return;
		}
	}

	@RequiredArgsConstructor
	private class CDLRunnable implements Runnable {

		private final ThreadPool pool;
		private final Runnable runnable;

		@Override
		public void run() {
			pool.await();
			this.runnable.run();
		}

	}

}
