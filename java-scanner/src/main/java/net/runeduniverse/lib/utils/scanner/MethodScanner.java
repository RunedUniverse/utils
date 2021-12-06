package net.runeduniverse.lib.utils.scanner;

import java.lang.reflect.Method;

public class MethodScanner<M extends MethodPattern> implements IMethodScanner<M> {

	protected final PatternCreator<M> creator;
	protected final ScanOrder order;

	public MethodScanner(PatternCreator<M> creator) {
		this.creator = creator;
		this.order = ScanOrder.ALL;
	}

	public MethodScanner(PatternCreator<M> creator, ScanOrder order) {
		this.creator = creator;
		this.order = order;
	}

	protected static MethodPattern createPattern(Method method) {
		return new MethodPattern(method);
	}

	@Override
	public void scan(Method method, Class<?> type, TypePattern<?, M> pattern) throws Exception {
		pattern.getMethods()
				.put(null, this.creator.createPattern(method));
	}

	@FunctionalInterface
	public static interface PatternCreator<M extends MethodPattern> {
		M createPattern(Method method) throws Exception;
	}

	public static MethodScanner<MethodPattern> DEFAULT() {
		return new MethodScanner<MethodPattern>(MethodScanner::createPattern);
	}

	public static MethodScanner<MethodPattern> DEFAULT(ScanOrder order) {
		return new MethodScanner<MethodPattern>(MethodScanner::createPattern, order);
	}

}
