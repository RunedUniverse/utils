package net.runeduniverse.libs.utils.async;

public interface Chainable<CHAIN> extends Threadable {

	// SETTINGS
	CHAIN burying(boolean buryInterruptedException);

	// METHODS
	<C extends Chainable<?>> C append(C descendant);

	@Deprecated
	void execute();

	// SETTER
	@Deprecated
	void setPrecedent(Chainable<?> precedent);
}
