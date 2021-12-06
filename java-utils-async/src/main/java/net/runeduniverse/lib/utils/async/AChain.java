package net.runeduniverse.lib.utils.async;

import lombok.Setter;

public abstract class AChain<CHAIN extends IChainable<?>> implements IChainable<CHAIN> {

	protected boolean buryInterruptedException = true;
	protected boolean stopAsap = false;
	protected boolean ignoreStop = false;
	@Setter
	protected IChainable<?> precedent = null;
	protected IChainable<?> descendant = null;

	protected CHAIN instance = null;

	// SETTINGS
	@Override
	public CHAIN burying(boolean buryInterruptedException) {
		this.buryInterruptedException = buryInterruptedException;
		if (this.descendant != null)
			this.descendant.burying(buryInterruptedException);
		return instance;
	}

	// METHODS
	@SuppressWarnings("deprecation")
	protected void done() {
		if (this.isStopping() || this.descendant == null)
			return;
		this.descendant.execute();
	}

	protected boolean handle(Exception e) {
		if (e instanceof InterruptedException && this.buryInterruptedException)
			return true;
		e.printStackTrace();
		return false;
	}

	protected boolean isStopping() {
		return this.ignoreStop ? false : this.stopAsap;
	}

	@SuppressWarnings("deprecation")
	@Override
	public <C extends IChainable<?>> C append(C descendant) {
		descendant.setPrecedent(this);
		this.descendant = descendant;
		descendant.burying(buryInterruptedException);
		return descendant;
	}

	@Override
	public void stop() {
		if (this.stopAsap)
			return;
		this.stopAsap = true;
		if (this.precedent != null)
			this.precedent.stop();
		if (this.descendant != null)
			this.descendant.stop();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		if (this.isStopping())
			return;
		if (this.precedent == null)
			this.instance.execute();
		else
			this.precedent.run();
	}

}
