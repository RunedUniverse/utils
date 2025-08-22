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
package net.runeduniverse.lib.utils.scanner.pattern.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

public interface FieldPattern {

	public Field getField();

	public Class<?> getType();

	public boolean isCollection();

	public void setValue(Object entity, Object value) throws IllegalArgumentException;

	public void putValue(Object entity, Object value) throws IllegalArgumentException;

	public Object getValue(Object entity) throws IllegalArgumentException;

	public void removeValues(Object entity, Collection<Object> deletedEntities);

	public void clearValue(Object entity);

	public <A extends Annotation> A getAnno(Class<A> annoType);

}
