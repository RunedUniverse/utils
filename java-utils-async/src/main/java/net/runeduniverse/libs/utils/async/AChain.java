package net.runeduniverse.libs.utils.async;

import lombok.Setter;

public abstract class AChain<CHAIN extends Chainable<?>> implements Chainable<CHAIN> {

	protected boolean buryInterruptedException = true;
	@Setter
	protected Chainable<?> precedent = null;
	protected Chainable<?> descendant = null;

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
		if (descendant != null)
			descendant.execute();
	}

	protected boolean handle(Exception e) {
		if (e instanceof InterruptedException && this.buryInterruptedException)
			return true;
		e.printStackTrace();
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public <C extends Chainable<?>> C append(C descendant) {
		descendant.setPrecedent(this);
		this.descendant = descendant;
		descendant.burying(buryInterruptedException);
		return descendant;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		if (this.precedent == null)
			instance.execute();
		else
			this.precedent.run();
	}

}
