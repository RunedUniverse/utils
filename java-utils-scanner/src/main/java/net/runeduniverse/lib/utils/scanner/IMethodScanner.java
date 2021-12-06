package net.runeduniverse.lib.utils.scanner;

import java.lang.reflect.Method;

public interface IMethodScanner<M extends MethodPattern> {
	void scan(Method method, Class<?> type, TypePattern<?, M> pattern) throws Exception;

}
