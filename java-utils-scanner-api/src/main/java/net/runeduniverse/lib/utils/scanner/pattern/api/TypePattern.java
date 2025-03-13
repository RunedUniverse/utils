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
package net.runeduniverse.lib.utils.scanner.pattern.api;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

public interface TypePattern<F extends FieldPattern, M extends MethodPattern> {

	public String getPkg();

	public ClassLoader getLoader();

	public Class<?> getType();

	public Class<?> getSuperType();

	public Set<Class<? extends Annotation>> getFieldAnnotations();

	public Set<Class<? extends Annotation>> getMethodAnnotations();

	public boolean hasFields(Class<? extends Annotation> anno);

	@SuppressWarnings("unchecked")
	public boolean hasAllFields(Class<? extends Annotation>... annos);

	@SuppressWarnings("unchecked")
	public boolean hasAnyFields(Class<? extends Annotation>... annos);

	public default boolean hasNeutralFields() {
		return hasFields(getNeutralAnnotation());
	}

	public boolean hasMethods(Class<? extends Annotation> anno);

	@SuppressWarnings("unchecked")
	public boolean hasAllMethods(Class<? extends Annotation>... annos);

	@SuppressWarnings("unchecked")
	public boolean hasAnyMethods(Class<? extends Annotation>... annos);

	public default boolean hasNeutralMethods() {
		return hasMethods(getNeutralAnnotation());
	}

	public F getField(Class<? extends Annotation> anno);

	public Set<F> getFields(Class<? extends Annotation> anno);

	public Set<F> getFields();

	public Map<F, Set<Class<? extends Annotation>>> mapFields();

	public M getMethod(Class<? extends Annotation> anno);

	public Set<M> getMethods(Class<? extends Annotation> anno);

	public Set<M> getMethods();

	public Map<M, Set<Class<? extends Annotation>>> mapMethods();

	/**
	 * Used to call parsed Methods
	 *
	 * @param anno
	 * @param obj
	 * @return {@code true} if successfull
	 */
	public boolean callMethod(Class<? extends Annotation> anno, Object obj, Object... args);

	public default Class<? extends Annotation> getNeutralAnnotation() {
		return NeutralAnnotation.class;
	}

	public default boolean isNeutralAnnotation(Class<? extends Annotation> anno) {
		return NeutralAnnotation.class.isAssignableFrom(anno);
	}

	/**
	 * Following Annotation class is used to reference all Fields and Methods which
	 * are not annotated
	 */
	public static @interface NeutralAnnotation {
	}

}
