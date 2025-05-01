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
package net.runeduniverse.lib.utils.maven3.ext.config;

import java.util.Properties;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import net.runeduniverse.lib.utils.config.api.Property;
import net.runeduniverse.lib.utils.config.api.PropertyStore;

import static net.runeduniverse.lib.utils.config.api.Property.defaultKey;
import static net.runeduniverse.lib.utils.config.api.Property.key;

public class ConfigBuilder<T extends PropertyStore> extends net.runeduniverse.lib.utils.config.ConfigBuilder<T> {

	public ConfigBuilder(final T propertyStore) {
		super(propertyStore);
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
