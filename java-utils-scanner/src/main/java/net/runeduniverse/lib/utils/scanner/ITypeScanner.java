package net.runeduniverse.lib.utils.scanner;

public interface ITypeScanner {
	void scan(Class<?> type, ClassLoader loader, String pkg) throws Exception;
}
