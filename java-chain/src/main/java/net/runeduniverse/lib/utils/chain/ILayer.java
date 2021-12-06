package net.runeduniverse.lib.utils.chain;

public interface ILayer {
	void call(ChainRuntime<?> runtime) throws Exception;
}
