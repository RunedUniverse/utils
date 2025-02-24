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
package net.runeduniverse.lib.utils.maven.ext.config;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import net.runeduniverse.lib.utils.maven.ext.config.api.Property;
import net.runeduniverse.lib.utils.maven.ext.config.api.PropertyStore;

public abstract class APropertyStore implements PropertyStore {

	protected final Map<String, Property<?>> properties;

	public APropertyStore() {
		this.properties = new LinkedHashMap<>();
	}

	public APropertyStore(final Map<String, Property<?>> properties) {
		this.properties = properties;
	}

	protected void redirectSetProperty(final String id, final Property<?> value) {
		if (value == null)
			this.properties.remove(id);
		else
			this.properties.put(id, value);
	}

	@Override
	public Collection<Property<?>> getAllProperties() {
		return Collections.unmodifiableCollection(this.properties.values());
	}

	@Override
	public Property<?> getProperty(final String id) {
		return this.properties.get(id);
	}

	@Override
	public void setProperty(final Property<?> value) {
		if (value == null)
			return;
		final String id = value.getId();
		if (id == null)
			return;
		redirectSetProperty(id, value);
	}

	@Override
	public void removeProperty(final String id) {
		if (id == null)
			return;
		redirectSetProperty(id, null);
	}

	@Override
	public void removeProperty(final Property<?> value) {
		if (value == null)
			return;
		final String id = value.getId();
		if (id == null)
			return;
		redirectSetProperty(id, null);
	}

	@Override
	public void resolveSelections() {
		for (Property<?> property : this.properties.values()) {
			if (property != null)
				property.resolveSelection();
		}
	}

	@SuppressWarnings("unchecked")
	protected static <T> boolean tryCast(final Class<T> type, final Property<?> property,
			final Consumer<Property<T>> consumer) {
		if (type == null || consumer == null)
			return false;
		if (property == null) {
			consumer.accept(null);
			return true;
		}
		try {
			consumer.accept((Property<T>) property);
			return true;
		} catch (ClassCastException ignored) {
			return false;
		}
	}
}
