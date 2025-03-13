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
package net.runeduniverse.lib.utils.maven.ext.config;

import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import net.runeduniverse.lib.utils.maven.ext.config.api.Property;
import net.runeduniverse.lib.utils.maven.ext.config.api.PropertyStore;

import static net.runeduniverse.lib.utils.maven.ext.config.api.Property.key;
import static net.runeduniverse.lib.utils.maven.ext.config.api.Property.defaultKey;

public class ConfigBuilder<T extends PropertyStore> {

	protected final Set<Consumer<Properties>> defaultPropertySetup = new LinkedHashSet<>();

	private final T propertyStore;

	private Properties properties = null;

	public ConfigBuilder(final T propertyStore) {
		this.propertyStore = propertyStore;
	}

	protected Properties newProperties() {
		return new Properties();
	}

	protected Property<Boolean> newBooleanProperty(final String key) {
		return new AbstractProperty<>(key);
	}

	protected Property<String> newStringProperty(final String key) {
		return new AbstractProperty<>(key);
	}

	protected Properties getDefaultProperties() {
		final Properties properties = newProperties();
		for (Consumer<Properties> setup : this.defaultPropertySetup)
			setup.accept(properties);
		return properties;
	}

	public Properties getProperties() {
		if (this.properties == null)
			return this.properties = getDefaultProperties();
		return this.properties;
	}

	public T getPropertyStore() {
		return this.propertyStore;
	}

	public void withDefaultProperty(final String key, final String value) {
		this.defaultPropertySetup.add(p -> p.setProperty(defaultKey(key), value));
	}

	@SuppressWarnings("unchecked")
	public void withProperties(final Supplier<Properties>... supplier) {
		if (supplier == null)
			return;
		final Properties properties = getProperties();
		for (Supplier<Properties> s : supplier) {
			final Properties other = s.get();
			if (other == null)
				continue;
			properties.putAll(other);
		}
	}

	public void parseBooleanProperty(final String key) {
		final Properties properties = getProperties();
		final Property<Boolean> property = newBooleanProperty(key);
		final String defaultValue = properties.getProperty(defaultKey(key));
		final String selectedValue = properties.getProperty(key(key));
		property.setDefault(defaultValue == null ? null : "true".equalsIgnoreCase(defaultValue));
		property.setSelected(selectedValue == null ? null : "true".equalsIgnoreCase(selectedValue));
		property.addOptions(true, false);
		getPropertyStore().setProperty(property);
	}

	public void parseTextProperty(final String key, final String... options) {
		final Properties properties = getProperties();
		final Property<String> property = newStringProperty(key);
		property.setDefault(properties.getProperty(defaultKey(key)));
		property.setSelected(properties.getProperty(key(key)));
		property.addOptions(options);
		getPropertyStore().setProperty(property);
	}

	public void parsePlexusHintProperty(final String key, final PlexusContainer container, final Class<?> plexusRole) {
		final Properties properties = getProperties();
		final Property<String> property = newStringProperty(key);
		property.setDefault(properties.getProperty(defaultKey(key)));
		property.setSelected(properties.getProperty(key(key)));
		try {
			property.addOptions(container.lookupMap(plexusRole)
					.keySet());
		} catch (ComponentLookupException ignored) {
		}
		getPropertyStore().setProperty(property);
	}
}
