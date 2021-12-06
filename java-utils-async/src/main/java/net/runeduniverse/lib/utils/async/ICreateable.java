package net.runeduniverse.lib.utils.async;

public interface ICreateable<KEY> {

	public IChainable<?> create(KEY key);
}
