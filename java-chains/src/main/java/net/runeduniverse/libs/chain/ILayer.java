package net.runeduniverse.libs.chain;

public interface ILayer {
	void call(ChainRuntime<?> runtime) throws Exception;
}
