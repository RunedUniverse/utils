/*
 * Copyright © 2025 VenaNocta (venanocta@gmail.com)
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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.Getter;
import net.runeduniverse.lib.utils.scanner.pattern.api.FieldPattern;
import net.runeduniverse.lib.utils.scanner.pattern.api.MethodPattern;
import net.runeduniverse.lib.utils.scanner.pattern.api.TypePattern;

import static net.runeduniverse.lib.utils.common.CollectionUtils.firstNotNull;

public class DefaultTypePattern<F extends FieldPattern, M extends MethodPattern> implements TypePattern<F, M> {

	protected final Map<Class<? extends Annotation>, Set<F>> fields;
	protected final Map<Class<? extends Annotation>, Set<M>> methods;

	@Getter
	protected final String pkg;
	@Getter
	protected final ClassLoader loader;
	@Getter
	protected final Class<?> type;
	@Getter
	protected final Class<?> superType;

	public DefaultTypePattern(String pkg, ClassLoader loader, Class<?> type) {
		this(new LinkedHashMap<>(), new LinkedHashMap<>(), pkg, loader, type);
	}

	protected DefaultTypePattern(Map<Class<? extends Annotation>, Set<F>> fields,
			Map<Class<? extends Annotation>, Set<M>> methods, String pkg, ClassLoader loader, Class<?> type) {
		this.fields = fields;
		this.methods = methods;
		this.pkg = pkg;
		this.loader = loader;
		this.type = type;
		this.superType = type.getSuperclass();
	}

	@Override
	public Set<Class<? extends Annotation>> getFieldAnnotations() {
		final Set<Class<? extends Annotation>> keys = new LinkedHashSet<>();
		for (Map.Entry<Class<? extends Annotation>, Set<F>> entry : this.fields.entrySet()) {
			final Class<? extends Annotation> anno = entry.getKey();
			if (isNeutralAnnotation(anno))
				continue;
			final Set<F> col = entry.getValue();
			if (col == null || col.isEmpty())
				continue;
			keys.add(anno);
		}
		return Collections.unmodifiableSet(keys);
	}

	@Override
	public Set<Class<? extends Annotation>> getMethodAnnotations() {
		final Set<Class<? extends Annotation>> keys = new LinkedHashSet<>();
		for (Map.Entry<Class<? extends Annotation>, Set<M>> entry : this.methods.entrySet()) {
			final Class<? extends Annotation> anno = entry.getKey();
			if (isNeutralAnnotation(anno))
				continue;
			final Set<M> col = entry.getValue();
			if (col == null || col.isEmpty())
				continue;
			keys.add(anno);
		}
		return Collections.unmodifiableSet(keys);
	}

	@Override
	public boolean hasFields(Class<? extends Annotation> anno) {
		if (anno == null) {
			anno = getNeutralAnnotation();
		}
		final Set<F> annoFields = this.fields.get(anno);
		if (annoFields == null)
			return false;
		return !annoFields.isEmpty();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean hasAllFields(Class<? extends Annotation>... annos) {
		for (Class<? extends Annotation> anno : annos) {
			if (anno == null) {
				anno = getNeutralAnnotation();
			}
			if (!hasFields(anno))
				return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean hasAnyFields(Class<? extends Annotation>... annos) {
		for (Class<? extends Annotation> anno : annos) {
			if (anno == null) {
				anno = getNeutralAnnotation();
			}
			if (hasFields(anno))
				return true;
		}
		return false;
	}

	@Override
	public boolean hasMethods(Class<? extends Annotation> anno) {
		if (anno == null) {
			anno = getNeutralAnnotation();
		}
		final Set<M> annoMethods = this.methods.get(anno);
		if (annoMethods == null)
			return false;
		return !annoMethods.isEmpty();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean hasAllMethods(Class<? extends Annotation>... annos) {
		for (Class<? extends Annotation> anno : annos) {
			if (anno == null) {
				anno = getNeutralAnnotation();
			}
			if (!hasFields(anno))
				return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean hasAnyMethods(Class<? extends Annotation>... annos) {
		for (Class<? extends Annotation> anno : annos) {
			if (anno == null) {
				anno = getNeutralAnnotation();
			}
			if (hasFields(anno))
				return true;
		}
		return false;
	}

	@Override
	public F getField(Class<? extends Annotation> anno) {
		if (anno == null) {
			anno = getNeutralAnnotation();
		}
		final Set<F> annoFields = this.fields.get(anno);
		if (annoFields == null)
			return null;
		return firstNotNull(annoFields);
	}

	@Override
	public Set<F> getFields(Class<? extends Annotation> anno) {
		if (anno == null) {
			anno = getNeutralAnnotation();
		}
		Set<F> annoFields = this.fields.get(anno);
		if (annoFields == null) {
			this.fields.put(anno, annoFields = new LinkedHashSet<>(0));
		}
		return annoFields;
	}

	@Override
	public Set<F> getFields() {
		final Set<F> fields = new LinkedHashSet<>();
		for (Set<F> col : this.fields.values()) {
			fields.addAll(col);
		}
		return Collections.unmodifiableSet(fields);
	}

	@Override
	public Map<F, Set<Class<? extends Annotation>>> mapFields() {
		final Map<F, Set<Class<? extends Annotation>>> map = new LinkedHashMap<>();

		for (Entry<Class<? extends Annotation>, Set<F>> entry : this.fields.entrySet()) {
			final Class<? extends Annotation> anno = entry.getKey();
			if (isNeutralAnnotation(anno))
				continue;

			final Set<F> col = entry.getValue();
			if (col == null)
				continue;

			for (F field : col) {
				Set<Class<? extends Annotation>> annos = map.get(field);
				if (annos == null) {
					map.put(field, annos = new LinkedHashSet<>());
				}
				annos.add(anno);
			}
		}
		return map;
	}

	@Override
	public M getMethod(Class<? extends Annotation> anno) {
		if (anno == null) {
			anno = getNeutralAnnotation();
		}
		final Set<M> annoMethods = this.methods.get(anno);
		if (annoMethods == null)
			return null;
		return firstNotNull(annoMethods);
	}

	@Override
	public Set<M> getMethods(Class<? extends Annotation> anno) {
		if (anno == null) {
			anno = getNeutralAnnotation();
		}
		Set<M> annoMethods = this.methods.get(anno);
		if (annoMethods == null) {
			this.methods.put(anno, annoMethods = new LinkedHashSet<>(0));
		}
		return annoMethods;
	}

	@Override
	public Set<M> getMethods() {
		final Set<M> methods = new LinkedHashSet<>();
		for (Set<M> col : this.methods.values()) {
			methods.addAll(col);
		}
		return Collections.unmodifiableSet(methods);
	}

	@Override
	public Map<M, Set<Class<? extends Annotation>>> mapMethods() {
		final Map<M, Set<Class<? extends Annotation>>> map = new LinkedHashMap<>();

		for (Entry<Class<? extends Annotation>, Set<M>> entry : this.methods.entrySet()) {
			final Class<? extends Annotation> anno = entry.getKey();
			if (isNeutralAnnotation(anno))
				continue;

			final Set<M> col = entry.getValue();
			if (col == null)
				continue;

			for (M method : col) {
				Set<Class<? extends Annotation>> annos = map.get(method);
				if (annos == null) {
					map.put(method, annos = new LinkedHashSet<>());
				}
				annos.add(anno);
			}
		}
		return map;
	}

	@Override
	public boolean callMethod(Class<? extends Annotation> anno, Object obj, Object... args) {
		final MethodPattern method = getMethod(anno);
		return (obj == null || method == null) ? false : method.invoke(obj, args);
	}

}
