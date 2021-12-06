package net.runeduniverse.lib.utils.scanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class MethodAnnotationScanner<M extends MethodPattern> extends MethodScanner<M> {

	private final Class<? extends Annotation> anno;

	public MethodAnnotationScanner(PatternCreator<M> creator, Class<? extends Annotation> anno) {
		super(creator);
		this.anno = anno;
	}

	public MethodAnnotationScanner(PatternCreator<M> creator, Class<? extends Annotation> anno, ScanOrder order) {
		super(creator, order);
		this.anno = anno;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void scan(Method method, Class<?> type, TypePattern<?, M> pattern) throws Exception {
		switch (this.order) {
		case FIRST:
			if (pattern.hasMethods(this.anno))
				return;
		case LAST:
			pattern.getFields()
					.remove(this.anno);
		case ALL:
			if (method.isAnnotationPresent(this.anno))
				super.scan(method, type, pattern);
		}
	}

	public static MethodAnnotationScanner<MethodPattern> DEFAULT(Class<? extends Annotation> anno) {
		return new MethodAnnotationScanner<MethodPattern>(MethodScanner::createPattern, anno);
	}

	public static MethodAnnotationScanner<MethodPattern> DEFAULT(Class<? extends Annotation> anno, ScanOrder order) {
		return new MethodAnnotationScanner<MethodPattern>(MethodScanner::createPattern, anno, order);
	}

}
