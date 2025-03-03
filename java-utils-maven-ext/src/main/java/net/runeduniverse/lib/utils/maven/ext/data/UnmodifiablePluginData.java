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
package net.runeduniverse.lib.utils.maven.ext.data;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;

import net.runeduniverse.lib.utils.maven.ext.data.api.PluginData;

import static net.runeduniverse.lib.utils.common.CollectionUtils.unmodifiable;

public class UnmodifiablePluginData implements PluginData {

	private final Map<MavenProject, Set<Plugin>> map;
	private final Set<Plugin> all;

	/**
	 * Wrap {@code Map<MavenProject, Set<Plugin>> map} as UnmodifiableData.
	 *
	 * @param extensions as unmodifiable data
	 * @throws NullPointerException if {@code plugins} is {@code null}
	 */
	public UnmodifiablePluginData(final Map<MavenProject, Set<Plugin>> plugins) {
		this(plugins, LinkedHashMap::new, LinkedHashSet::new);
	}

	/**
	 * Wrap {@code Map<MavenProject, Set<Plugin>> map} as UnmodifiableData.
	 *
	 * @param plugins     as unmodifiable data
	 * @param mapSupplier map-factory
	 * @param setSupplier value-factory
	 * @throws NullPointerException if any of [ {@code plugins},
	 *                              {@code mapSupplier}, {@code setSupplier}] is
	 *                              {@code null}
	 */
	public UnmodifiablePluginData(final Map<MavenProject, Set<Plugin>> plugins,
			Supplier<Map<MavenProject, Set<Plugin>>> mapSupplier, Supplier<Set<Plugin>> setSupplier) {
		Objects.requireNonNull(plugins);
		Objects.requireNonNull(mapSupplier);
		Objects.requireNonNull(setSupplier);

		final Map<MavenProject, Set<Plugin>> map = mapSupplier.get();
		final Set<Plugin> all = setSupplier.get();
		for (Entry<MavenProject, Set<Plugin>> entry : plugins.entrySet()) {
			final Set<Plugin> set = entry.getValue();
			if (set == null || set.isEmpty())
				continue;
			final Set<Plugin> nset = setSupplier.get();
			nset.addAll(set);
			all.addAll(set);
			map.put(entry.getKey(), unmodifiable(nset));
		}

		this.map = unmodifiable(map);
		this.all = unmodifiable(all);
	}

	@Override
	public Set<Plugin> getPlugins() {
		return this.all;
	}

	@Override
	public Set<Plugin> getPlugins(final MavenProject mvnProject) {
		return this.map.get(mvnProject);
	}

	@Override
	public Map<MavenProject, Set<Plugin>> getPluginMap() {
		return this.map;
	}
}
