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
package net.runeduniverse.lib.utils.config;

import java.util.LinkedHashSet;
import java.util.Set;

import net.runeduniverse.lib.utils.config.api.Property;

public class AbstractProperty<T> implements Property<T> {

	protected final String id;
	protected final Set<T> options;

	protected T defaultValue = null;
	protected T selectedValue = null;

	public AbstractProperty(final String id) {
		this.id = id;
		this.options = new LinkedHashSet<>();
	}

	public AbstractProperty(final String id, final Set<T> options) {
		this.id = id;
		this.options = options;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public T getDefault() {
		return this.defaultValue;
	}

	@Override
	public T getSelected() {
		return this.selectedValue;
	}

	@Override
	public Set<T> getOptions() {
		return this.options;
	}

	@Override
	public void setDefault(T value) {
		this.defaultValue = value;
	}

	@Override
	public void setSelected(T value) {
		this.selectedValue = value;
	}

	@Override
	public void addOption(T value) {
		this.options.add(value);
	}
}
