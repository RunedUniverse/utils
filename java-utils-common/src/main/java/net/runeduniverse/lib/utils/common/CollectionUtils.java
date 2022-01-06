/*
 * Copyright © 2022 Pl4yingNight (pl4yingnight@gmail.com)
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

import java.util.Collection;

public class CollectionUtils {

	public static final <T> T first(Collection<T> collection) {
		for (T t : collection)
			return t;
		return null;
	}

	public static final <T> T firstNotNull(Collection<T> collection) {
		for (T t : collection)
			if (t != null)
				return t;
		return null;
	}
}
