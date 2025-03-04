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
package net.runeduniverse.lib.utils.maven;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;
import org.eclipse.sisu.inject.Logs;

public class SessionContextUtils {

	public static final String DEFAULT_HINT = "default";

	private SessionContextUtils() {
	}

	@SuppressWarnings("unchecked")
	public static <R> Map<String, R> getSessionContext(final MavenSession mvnSession, final Class<R> role) {
		final Object obj = mvnSession.getCurrentProject()
				.getContextValue(role.getCanonicalName());
		if (obj == null)
			return null;
		try {
			return (Map<String, R>) obj;
		} catch (ClassCastException e) {
			return null;
		}
	}

	public static <R> Map<String, R> putSessionContext(final MavenSession mvnSession, final Class<R> role,
			final Map<String, R> context) {
		final Map<String, R> oldContext = getSessionContext(mvnSession, role);
		mvnSession.getCurrentProject()
				.setContextValue(role.getCanonicalName(), context);
		return oldContext;
	}

	public static <R> void releaseSessionContext(final MavenSession mvnSession, final Class<R> role) {
		final Map<String, R> context = getSessionContext(mvnSession, role);
		if (context != null) {
			for (R component : context.values()) {
				releaseSessionComponent(mvnSession, role, component);
			}
			mvnSession.getCurrentProject()
					.setContextValue(role.getCanonicalName(), null);
		}
	}

	public static <R> R loadSessionComponent(final MavenSession mvnSession, final Class<R> role) {
		final Map<String, R> map = getSessionContext(mvnSession, role);
		if (map == null)
			return null;
		R val = map.get(DEFAULT_HINT);
		if (val != null)
			return val;
		for (Iterator<R> i = map.values()
				.iterator(); i.hasNext();) {
			val = i.next();
			if (val != null)
				return val;
		}
		return null;
	}

	public static <R> R loadSessionComponent(final MavenSession mvnSession, final Class<R> role, final String hint) {
		final Map<String, R> map = getSessionContext(mvnSession, role);
		if (map == null)
			return null;
		return map.get(hint);
	}

	public static <R, T extends R> void putSessionComponent(final MavenSession mvnSession, final Class<R> role,
			final T component) {
		putSessionComponent(mvnSession, role, "default", component);
	}

	public static <R, T extends R> void putSessionComponent(final MavenSession mvnSession, final Class<R> role,
			final String hint, final T component) {
		Map<String, R> map = getSessionContext(mvnSession, role);
		if (map == null) {
			map = new LinkedHashMap<>();
			putSessionContext(mvnSession, role, map);
		}
		map.put(hint, component);
	}

	public static <R, T extends R> void releaseSessionComponent(final MavenSession mvnSession, final Class<R> role,
			final T component) {
		final Map<String, R> map = getSessionContext(mvnSession, role);
		if (component != null) {
			if (component instanceof Startable)
				try {
					((Startable) component).stop();
				} catch (StoppingException e) {
					Logs.catchThrowable(e);
				}
			if (component instanceof Disposable)
				((Disposable) component).dispose();
		}
		if (map == null)
			return;
		map.values()
				.remove(component);
	}

}
