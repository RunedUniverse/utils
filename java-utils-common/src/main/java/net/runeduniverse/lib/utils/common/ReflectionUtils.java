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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ReflectionUtils {

	public static <R, H, E> R supplyWithHandler(final Map<String, H> handlerMap, final E entity,
			final Function<H, R> converter, final int maxSearchDepth) {
		if (handlerMap == null || entity == null || converter == null)
			return null;

		final List<String> types = collectCanonicalNamesOfClass(entity.getClass(), maxSearchDepth);

		for (String type : types) {
			// find valid handler
			final H handler = handlerMap.get(type);
			if (handler == null)
				continue;
			// check if the handler rejects the entry
			final R result = converter.apply(handler);
			if (result != null)
				return result;
		}
		return null;
	}

	public static List<String> collectCanonicalNamesOfClass(Class<?> clazz, final int maxSearchDepth) {
		final List<String> lst = new LinkedList<>();
		// add class
		lst.add(clazz.getCanonicalName());

		for (int i = 0; i < maxSearchDepth; i++) {
			if (clazz == Object.class)
				return lst;
			// add all interfaces
			for (Class<?> ic : clazz.getInterfaces()) {
				lst.add(ic.getCanonicalName());
			}
			// add superclass
			clazz = clazz.getSuperclass();
			lst.add(clazz.getCanonicalName());
		}
		return lst;
	}
}
