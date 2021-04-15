package net.runeduniverse.libs.scanner;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public class TypePattern<F extends FieldPattern, M extends MethodPattern> {

	@Getter
	protected final Map<Class<? extends Annotation>, F> fields = new HashMap<>();
	@Getter
	protected final Map<Class<? extends Annotation>, M> methods = new HashMap<>();

	@Getter
	protected final String pkg;
	@Getter
	protected final ClassLoader loader;
	@Getter
	protected final Class<?> type;
	@Getter
	protected final Class<?> superType;

	public TypePattern(String pkg, ClassLoader loader, Class<?> type) {
		this.pkg = pkg;
		this.loader = loader;
		this.type = type;
		this.superType = type.getSuperclass();
	}

	@SuppressWarnings("unchecked")
	public boolean hasFields(Class<? extends Annotation>... annos) {
		for (Class<? extends Annotation> anno : annos)
			if (!this.fields.containsKey(anno))
				return false;
		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean hasMethods(Class<? extends Annotation>... annos) {
		for (Class<? extends Annotation> anno : annos)
			if (!this.methods.containsKey(anno))
				return false;
		return true;
	}

	public F getField(Class<? extends Annotation> anno) {
		return this.fields.get(anno);
	}

	/**
	 * Used to call parsed Methods
	 * 
	 * @param anno
	 * @param obj
	 * @return {@code true} if successfull
	 */
	public boolean callMethod(Class<? extends Annotation> anno, Object obj, Object... args) {
		MethodPattern method = this.methods.get(anno);
		return (obj == null || method == null) ? false : method.invoke(obj, args);
	}
}
