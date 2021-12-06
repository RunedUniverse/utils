package net.runeduniverse.lib.utils.scanner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TypeScanner<F extends FieldPattern, M extends MethodPattern, T extends TypePattern<F, M>>
		implements ITypeScanner {

	protected final PatternCreator<F, M, T> creator;
	protected final ResultConsumer<F, M, T> consumer;

	protected SCN<F, M, T> scanFieldsFnc = TypeScanner::scanFields;
	protected SCN<F, M, T> scanMethodsFnc = TypeScanner::scanMethods;
	protected Set<IFieldScanner<F>> fieldScanner = new HashSet<>();
	protected Set<IMethodScanner<M>> methodScanner = new HashSet<>();

	protected static TypePattern<FieldPattern, MethodPattern> createPattern(Class<?> type, ClassLoader loader,
			String pkg) {
		return new TypePattern<FieldPattern, MethodPattern>(pkg, loader, type);
	}

	public TypeScanner<F, M, T> addFieldScanner(IFieldScanner<F> fieldScanner) {
		this.fieldScanner.add(fieldScanner);
		return this;
	}

	public TypeScanner<F, M, T> addFieldScanner(IMethodScanner<M> methodScanner) {
		this.methodScanner.add(methodScanner);
		return this;
	}

	@Override
	public void scan(Class<?> type, ClassLoader loader, String pkg) throws Exception {
		T pattern = this.creator.createPattern(type, loader, pkg);
		TypeScanner.cascade(this, this.scanFieldsFnc, pattern, pattern.getType());
		TypeScanner.cascade(this, this.scanMethodsFnc, pattern, pattern.getType());
		this.consumer.accept(pattern);
	}

	@FunctionalInterface
	public static interface ResultConsumer<F extends FieldPattern, M extends MethodPattern, T extends TypePattern<F, M>> {
		void accept(T pattern) throws Exception;
	}

	@FunctionalInterface
	protected static interface SCN<F extends FieldPattern, M extends MethodPattern, T extends TypePattern<F, M>> {
		void accept(TypeScanner<F, M, T> scanner, T pattern, Class<?> type) throws Exception;
	}

	protected static <F extends FieldPattern, M extends MethodPattern, T extends TypePattern<F, M>> void cascade(
			TypeScanner<F, M, T> scanner, SCN<F, M, T> scn, T pattern, Class<?> type) throws Exception {
		scn.accept(scanner, pattern, type);
		if (type.getSuperclass()
				.equals(Object.class))
			return;
		cascade(scanner, scn, pattern, type.getSuperclass());
	}

	protected static <F extends FieldPattern, M extends MethodPattern, T extends TypePattern<F, M>> void scanFields(
			TypeScanner<F, M, T> scanner, T pattern, Class<?> type) throws Exception {
		for (Field f : type.getDeclaredFields())
			for (IFieldScanner<F> fscan : scanner.fieldScanner)
				fscan.scan(f, type, pattern);
	}

	protected static <F extends FieldPattern, M extends MethodPattern, T extends TypePattern<F, M>> void scanMethods(
			TypeScanner<F, M, T> scanner, T pattern, Class<?> type) throws Exception {
		for (Method m : type.getDeclaredMethods())
			for (IMethodScanner<M> mscan : scanner.methodScanner)
				mscan.scan(m, type, pattern);
	}

	@FunctionalInterface
	public static interface PatternCreator<F extends FieldPattern, M extends MethodPattern, T extends TypePattern<F, M>> {
		T createPattern(Class<?> type, ClassLoader loader, String pkg) throws Exception;
	}

	public static TypeScanner<FieldPattern, MethodPattern, TypePattern<FieldPattern, MethodPattern>> DEFAULT(
			ResultConsumer<FieldPattern, MethodPattern, TypePattern<FieldPattern, MethodPattern>> consumer) {
		return new TypeScanner<>(TypeScanner::createPattern, consumer);
	}
}
