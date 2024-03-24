/*
 * Copyright © 2024 VenaNocta (venanocta@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.runeduniverse.lib.utils.scanner.pattern;

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

	public boolean hasField(Class<? extends Annotation> anno) {
		return this.fields.containsKey(anno);
	}

	@SuppressWarnings("unchecked")
	public boolean hasFields(Class<? extends Annotation>... annos) {
		for (Class<? extends Annotation> anno : annos)
			if (!this.fields.containsKey(anno))
				return false;
		return true;
	}

	public boolean hasMethod(Class<? extends Annotation> anno) {
		return this.methods.containsKey(anno);
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
