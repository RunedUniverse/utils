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
package net.runeduniverse.lib.utils.plexus;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

public class PlexusContextUtils {

	private PlexusContextUtils() {
	}

	public static <T> List<ComponentDescriptor<T>> getPlexusComponentDescriptorList(final PlexusContainer container,
			final ClassRealm realm, final Class<T> type, final String role) {
		synchronized (container) {
			final ClassRealm oldLookupRealm = container.setLookupRealm(realm);
			final ClassLoader oldClassLoader = Thread.currentThread()
					.getContextClassLoader();
			Thread.currentThread()
					.setContextClassLoader(realm);
			try {
				return container.getComponentDescriptorList(type, role);
			} finally {
				Thread.currentThread()
						.setContextClassLoader(oldClassLoader);
				container.setLookupRealm(oldLookupRealm);
			}
		}
	}

	public static <T> Map<String, ComponentDescriptor<T>> getPlexusComponentDescriptorMap(
			final PlexusContainer container, final ClassRealm realm, final Class<T> type, final String role) {
		synchronized (container) {
			final ClassRealm oldLookupRealm = container.setLookupRealm(realm);
			final ClassLoader oldClassLoader = Thread.currentThread()
					.getContextClassLoader();
			Thread.currentThread()
					.setContextClassLoader(realm);
			try {
				return container.getComponentDescriptorMap(type, role);
			} finally {
				Thread.currentThread()
						.setContextClassLoader(oldClassLoader);
				container.setLookupRealm(oldLookupRealm);
			}
		}
	}

	public static boolean hasPlexusComponent(final PlexusContainer container, final ClassRealm realm,
			final Class<?> type, final ClassRealm... excludedRealms) {
		final Set<ComponentDescriptor<?>> excluded = new LinkedHashSet<>();
		for (ClassRealm excludedRealm : excludedRealms) {
			if (realm == excludedRealm)
				return false;
			excluded.addAll(getPlexusComponentDescriptorList(container, excludedRealm, type, null));
		}
		return !excluded.containsAll(getPlexusComponentDescriptorList(container, realm, type, null));
	}

	public static <T> T loadPlexusComponent(final PlexusContainer container, final ComponentDescriptor<T> descriptor)
			throws ComponentLookupException {
		synchronized (container) {
			final ClassRealm oldLookupRealm = container.setLookupRealm(descriptor.getRealm());
			final ClassLoader oldClassLoader = Thread.currentThread()
					.getContextClassLoader();
			Thread.currentThread()
					.setContextClassLoader(descriptor.getRealm());
			try {
				return container.lookup(descriptor.getImplementationClass(), descriptor.getRole(),
						descriptor.getRoleHint());
			} finally {
				Thread.currentThread()
						.setContextClassLoader(oldClassLoader);
				container.setLookupRealm(oldLookupRealm);
			}
		}
	}

	public static <T> void loadPlexusComponent(final PlexusContainer container, final ComponentDescriptor<T> descriptor,
			final ComponentHandler<T> handler) throws ComponentLookupException {
		synchronized (container) {
			final ClassRealm oldLookupRealm = container.setLookupRealm(descriptor.getRealm());
			final ClassLoader oldClassLoader = Thread.currentThread()
					.getContextClassLoader();
			Thread.currentThread()
					.setContextClassLoader(descriptor.getRealm());
			try {
				handler.accept(container, container.lookup(descriptor.getImplementationClass(), descriptor.getRole(),
						descriptor.getRoleHint()));
			} finally {
				Thread.currentThread()
						.setContextClassLoader(oldClassLoader);
				container.setLookupRealm(oldLookupRealm);
			}
		}
	}

	@FunctionalInterface
	public interface ComponentHandler<T> {

		public void accept(final PlexusContainer container, final T instance);

	}

}
