package net.runeduniverse.lib.utils.async;

public interface IChainable<CHAIN> extends IThreadable {

	// SETTINGS
	CHAIN burying(boolean buryInterruptedException);

	// METHODS
	<C extends IChainable<?>> C append(C descendant);

	void stop();

	@Deprecated
	void execute();

	// SETTER
	@Deprecated
	void setPrecedent(IChainable<?> precedent);
}
