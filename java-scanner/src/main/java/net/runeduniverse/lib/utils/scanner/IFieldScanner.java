package net.runeduniverse.lib.utils.scanner;

import java.lang.reflect.Field;

public interface IFieldScanner<F extends FieldPattern> {
	void scan(Field field, Class<?> type, TypePattern<F, ?> pattern) throws Exception;
}
