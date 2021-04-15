package net.runeduniverse.libs.scanner;

import java.lang.reflect.Field;

public class FieldScanner<F extends FieldPattern> implements IFieldScanner<F> {

	protected final PatternCreator<F> creator;
	protected final ScanOrder order;

	public FieldScanner(PatternCreator<F> creator) {
		this.creator = creator;
		this.order = ScanOrder.ALL;
	}

	public FieldScanner(PatternCreator<F> creator, ScanOrder order) {
		this.creator = creator;
		this.order = order;
	}

	protected static FieldPattern createPattern(Field field) {
		return new FieldPattern(field);
	}

	@Override
	public void scan(Field field, Class<?> type, TypePattern<F, ?> pattern) throws Exception {
		F p = this.creator.createPattern(field);
		if (p != null)
			pattern.getFields()
					.put(null, p);
	}

	@FunctionalInterface
	public static interface PatternCreator<F extends FieldPattern> {
		F createPattern(Field field) throws Exception;
	}

	public static FieldScanner<FieldPattern> DEFAULT() {
		return new FieldScanner<FieldPattern>(FieldScanner::createPattern);
	}

	public static FieldScanner<FieldPattern> DEFAULT(ScanOrder order) {
		return new FieldScanner<FieldPattern>(FieldScanner::createPattern, order);
	}
}
