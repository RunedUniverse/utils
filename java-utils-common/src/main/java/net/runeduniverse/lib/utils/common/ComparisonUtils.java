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
package net.runeduniverse.lib.utils.common;

import net.runeduniverse.lib.utils.common.api.Keyed;

public class ComparisonUtils {

	public static boolean typeIsAssignable(final Class<?> type, final Class<?> instanceType) {
		if (type == instanceType)
			return true;
		if (type == null || instanceType == null)
			return false;
		return type.isAssignableFrom(instanceType);
	}

	public static boolean objectEquals(final Object a, final Object b) {
		if (a == b)
			return true;
		if (a == null || b == null)
			return false;
		return a.equals(b);
	}

	public static boolean objectEquals(final Class<?> type, final Object a, final Object b) {
		if (a == b) {
			if (a == null)
				return true;
			return typeIsAssignable(type, a.getClass());
		}
		if (a == null || !typeIsAssignable(type, a.getClass()) || b == null || !typeIsAssignable(type, b.getClass()))
			return false;
		return a.equals(b);
	}

	public static boolean keyedEquals(final Keyed a, final Keyed b) {
		if (a == b)
			return true;
		if (a == null || b == null)
			return false;
		return objectEquals(a.key(), b.key());
	}

	public static <T extends Keyed> boolean keyedEquals(final Class<T> type, final Object a, final Object b) {
		if (a == b) {
			if (a == null)
				return true;
			return typeIsAssignable(type, a.getClass());
		}
		if (a == null || !typeIsAssignable(type, a.getClass()) || b == null || !typeIsAssignable(type, b.getClass()))
			return false;
		return objectEquals(((Keyed) a).key(), ((Keyed) b).key());
	}
}
