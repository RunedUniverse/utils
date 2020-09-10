package net.runeduniverse.libs.utils.async;

public interface Threadable extends Runnable {

	public default Thread asThread() {
		return new Thread(this);
	}

	public default Thread runAsThread() {
		Thread t = this.asThread();
		t.start();
		return t;
	}
}
