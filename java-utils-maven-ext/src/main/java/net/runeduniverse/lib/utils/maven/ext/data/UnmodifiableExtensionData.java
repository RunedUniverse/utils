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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.function.Supplier;

import org.apache.maven.project.MavenProject;

import net.runeduniverse.lib.utils.maven.ext.api.Extension;
import net.runeduniverse.lib.utils.maven.ext.data.api.ExtensionData;

public class UnmodifiableExtensionData implements ExtensionData {

	private final Map<MavenProject, Set<Extension>> map;
	private final Set<Extension> all;

	/**
	 * Wrap {@code Map<MavenProject, Set<Extension>> map} as UnmodifiableData.
	 *
	 * @param extensions as unmodifiable data
	 * @throws NullPointerException if {@code extensions} is {@code null}
	 */
	public UnmodifiableExtensionData(final Map<MavenProject, Set<Extension>> extensions) {
		this(extensions, LinkedHashMap::new, LinkedHashSet::new);
	}

	/**
	 * Wrap {@code Map<MavenProject, Set<Extension>> map} as UnmodifiableData.
	 *
	 * @param extensions as unmodifiable data
	 * @throws NullPointerException if any of [ {@code extensions},
	 *                              {@code mapSupplier}, {@code setSupplier}] is
	 *                              {@code null}
	 */
	public UnmodifiableExtensionData(final Map<MavenProject, Set<Extension>> extensions,
			Supplier<Map<MavenProject, Set<Extension>>> mapSupplier, Supplier<Set<Extension>> setSupplier) {
		Objects.requireNonNull(extensions);
		Objects.requireNonNull(mapSupplier);
		Objects.requireNonNull(setSupplier);

		Map<MavenProject, Set<Extension>> map = mapSupplier.get();
		Set<Extension> all = setSupplier.get();
		for (Entry<MavenProject, Set<Extension>> entry : extensions.entrySet()) {
			final Set<Extension> set = entry.getValue();
			if (set == null || set.isEmpty())
				continue;
			Set<Extension> nset = setSupplier.get();
			nset.addAll(set);
			all.addAll(set);

			if (nset instanceof NavigableSet)
				nset = Collections.unmodifiableNavigableSet((NavigableSet<Extension>) nset);
			else if (nset instanceof SortedSet)
				nset = Collections.unmodifiableSortedSet((SortedSet<Extension>) nset);
			else
				nset = Collections.unmodifiableSet(nset);

			map.put(entry.getKey(), nset);
		}

		if (map instanceof NavigableMap)
			map = Collections.unmodifiableNavigableMap((NavigableMap<MavenProject, Set<Extension>>) map);
		else if (map instanceof SortedMap)
			map = Collections.unmodifiableSortedMap((SortedMap<MavenProject, Set<Extension>>) map);
		else
			map = Collections.unmodifiableMap(map);

		if (all instanceof NavigableSet)
			all = Collections.unmodifiableNavigableSet((NavigableSet<Extension>) all);
		else if (all instanceof SortedSet)
			all = Collections.unmodifiableSortedSet((SortedSet<Extension>) all);
		else
			all = Collections.unmodifiableSet(all);

		this.map = map;
		this.all = all;
	}

	@Override
	public Set<Extension> getExtensions() {
		return this.all;
	}

	@Override
	public Set<Extension> getExtensions(final MavenProject mvnProject) {
		return this.map.get(mvnProject);
	}

	@Override
	public Map<MavenProject, Set<Extension>> getExtensionMap() {
		return this.map;
	}
}
