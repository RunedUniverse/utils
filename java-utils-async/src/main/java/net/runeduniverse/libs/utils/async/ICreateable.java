package net.runeduniverse.libs.utils.async;

public interface ICreateable<KEY> {

	public IChainable<?> create(KEY key);
}
