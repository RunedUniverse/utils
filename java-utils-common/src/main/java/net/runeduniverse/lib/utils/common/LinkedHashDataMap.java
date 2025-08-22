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

import java.util.LinkedHashMap;
import java.util.Map;

public class LinkedHashDataMap<K, V, D> extends AbstractDataMap<K, V, D> {

	public LinkedHashDataMap(final int initialCapacity, final float loadFactor) {
		super(new LinkedHashMap<>(initialCapacity, loadFactor));
	}

	public LinkedHashDataMap(final int initialCapacity) {
		super(new LinkedHashMap<>(initialCapacity));
	}

	public LinkedHashDataMap() {
		super(new LinkedHashMap<>());
	}

	public LinkedHashDataMap(final int initialCapacity, final float loadFactor, final boolean accessOrder) {
		super(new LinkedHashMap<>(initialCapacity, loadFactor, accessOrder));
	}

	@Override
	protected Map<K, V> createValueMap() {
		return new LinkedHashMap<>();
	}

	@Override
	protected Map<K, D> createDataMap() {
		return new LinkedHashMap<>();
	}

}
