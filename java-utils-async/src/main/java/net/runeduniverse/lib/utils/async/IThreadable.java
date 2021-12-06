package net.runeduniverse.lib.utils.async;

public interface IThreadable extends Runnable {

	public default Thread asThread() {
		return new Thread(this);
	}

	public default Thread runAsThread() {
		Thread t = this.asThread();
		t.start();
		return t;
	}
}
