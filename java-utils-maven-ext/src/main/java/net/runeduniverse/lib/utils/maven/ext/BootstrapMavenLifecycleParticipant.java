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
package net.runeduniverse.lib.utils.maven.ext;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.InvalidPluginDescriptorException;
import org.apache.maven.plugin.MavenPluginManager;
import org.apache.maven.plugin.PluginDescriptorParsingException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.DuplicateRealmException;
import org.codehaus.plexus.classworlds.realm.NoSuchRealmException;
import org.codehaus.plexus.component.annotations.Requirement;

import net.runeduniverse.lib.utils.maven.ext.data.DefaultExtension;
import net.runeduniverse.lib.utils.maven.ext.data.api.Extension;

public abstract class BootstrapMavenLifecycleParticipant extends AbstractMavenLifecycleParticipant {

	public static final String ERR_FAILED_TO_LOAD_MAVEN_EXTENSION_CLASSREALM = //
			"Failed to load maven-extension ClassRealm";

	public static final String REALM_ID_PLEXUS_CORE = "plexus.core";
	public static final String REALM_ID_MAVEN_EXT = "maven.ext";
	public static final String REALM_ID_CORE_EXT_PREFIX = "coreExtension>";
	public static final String REALM_ID_BUILD_EXT_PREFIX = "extension>";

	@Requirement
	protected MavenPluginManager mavenPluginManager;

	protected final Map<MavenProject, Set<Plugin>> unidentifiablePlugins;

	protected boolean coreExtension = false;

	protected BootstrapMavenLifecycleParticipant() {
		this.unidentifiablePlugins = new LinkedHashMap<>();
	}

	protected BootstrapMavenLifecycleParticipant(final Map<MavenProject, Set<Plugin>> unidentifiablePlugins) {
		Objects.nonNull(unidentifiablePlugins);
		this.unidentifiablePlugins = unidentifiablePlugins;
	}

	/**
	 * Invoked after MavenSession instance has been created.
	 *
	 * This callback is intended to allow extensions to inject execution properties,
	 * activate profiles and perform similar tasks that affect MavenProject instance
	 * construction.
	 */
	public void afterSessionStart(final MavenSession mvnSession) throws MavenExecutionException {
		this.coreExtension = true;
	}

	/**
	 * Invoked after all MavenProject instances have been created.
	 *
	 * This callback is intended to allow extensions to manipulate MavenProjects
	 * before they are sorted and actual build execution starts.
	 */
	public void afterProjectsRead(final MavenSession mvnSession) throws MavenExecutionException {
	}

	protected ClassRealm createExtensionRealm(final ClassRealm plexusCore, final ClassRealm currentRealm)
			throws DuplicateRealmException {
		return null;
	}

	protected boolean checkUnidentifiablePlugins() {
		for (Entry<MavenProject, Set<Plugin>> entry : this.unidentifiablePlugins.entrySet()) {
			final Set<Plugin> set = entry.getValue();
			if (set != null && !set.isEmpty())
				return true;
		}
		return false;
	}

	protected Set<Plugin> getUnidentifiablePlugins(final MavenProject mvnProject) {
		return this.unidentifiablePlugins.computeIfAbsent(mvnProject, k -> new LinkedHashSet<>());
	}

	protected boolean patchMaven(final MavenSession mvnSession, final boolean isCoreExt,
			final Set<Extension> extensions, final Set<Plugin> extPlugins) throws MavenExecutionException {
		return true;
	}

	protected boolean patchMaven(final MavenSession mvnSession) throws MavenExecutionException {
		_patchEventInfo_PatchingStarted();

		final ClassRealm currentRealm = (ClassRealm) Thread.currentThread()
				.getContextClassLoader();
		final ClassWorld world = currentRealm.getWorld();

		// extLoadState= 2 -> system core ext | 1 -> core ext | 0 -> build ext
		final short extLoadState;
		final boolean success;

		try {
			final ClassRealm plexusCore = world.getRealm(REALM_ID_PLEXUS_CORE);
			final ClassRealm realm;
			if (REALM_ID_PLEXUS_CORE.equals(currentRealm.getId())) {
				_patchEventInfo_SwitchRealmToPlexus();
				realm = currentRealm;
				extLoadState = 2;
			} else if (this.coreExtension) {
				_patchEventInfo_SwitchRealmToMavenExt();
				realm = world.getRealm(REALM_ID_MAVEN_EXT);
				extLoadState = 1;
			} else {
				realm = createExtensionRealm(plexusCore, currentRealm);
				if (realm == null) {
					_patchEventInfo_InvalidBuildExtension();
					return false;
				}
				_patchEventInfo_SwitchRealmToBuildExt();
				extLoadState = 0;
			}

			Thread.currentThread()
					.setContextClassLoader(realm);

			final Set<Extension> extensions = new LinkedHashSet<>();
			final Set<Plugin> extPlugins = new LinkedHashSet<>();

			extensions.addAll(scanCoreExtensions(world.getRealms()));

			for (MavenProject mvnProject : mvnSession.getAllProjects())
				extPlugins.addAll(discoverPlugins(mvnSession, mvnProject, extensions));

			if (!extensions.isEmpty()) {
				_patchEventInfo_ExtensionsDetected(mvnSession.getAllProjects(), extensions);
			}

			success = patchMaven(mvnSession, 0 < extLoadState, extensions, extPlugins);

		} catch (DuplicateRealmException | NoSuchRealmException e) {
			final MavenExecutionException ex = new MavenExecutionException(
					ERR_FAILED_TO_LOAD_MAVEN_EXTENSION_CLASSREALM, e);
			_patchEventError_PatchingAborted(ex);
			throw ex;
		} catch (MavenExecutionException ex) {
			_patchEventError_PatchingAborted(ex);
			throw ex;
		} finally {
			_patchEventInfo_ResetRealm();
			Thread.currentThread()
					.setContextClassLoader(currentRealm);
		}

		if (checkUnidentifiablePlugins()) {
			_patchEventInfo_UnidentifiablePluginsDetected(mvnSession.getAllProjects());
		}

		_patchEventInfo_PatchingFinished();
		return success;
	}

	// patchMaven() : Events
	protected void _patchEventInfo_PatchingStarted() {
	}

	protected void _patchEventInfo_InvalidBuildExtension() {
	}

	protected void _patchEventInfo_SwitchRealmToPlexus() {
	}

	protected void _patchEventInfo_SwitchRealmToMavenExt() {
	}

	protected void _patchEventInfo_SwitchRealmToBuildExt() {
	}

	protected void _patchEventInfo_ExtensionsDetected(final Collection<MavenProject> projects,
			final Collection<Extension> extensions) {
	}

	protected void _patchEventInfo_UnidentifiablePluginsDetected(final Collection<MavenProject> projects) {
	}

	protected void _patchEventError_PatchingAborted(final MavenExecutionException ex) {
	}

	protected void _patchEventInfo_ResetRealm() {
	}

	protected void _patchEventInfo_PatchingFinished() {
	}

	/**
	 * Invoked after all projects were built.
	 *
	 * This callback is intended to allow extensions to perform cleanup of any
	 * allocated external resources after the build. It is invoked on best-effort
	 * basis and may be missed due to an Error or RuntimeException in Maven core
	 * code.
	 *
	 * @since 3.2.1, MNG-5389
	 */
	public void afterSessionEnd(final MavenSession mvnSession) throws MavenExecutionException {
		// do nothing
	}

	protected Set<Plugin> discoverPlugins(final MavenSession mvnSession, final MavenProject mvnProject,
			final Set<Extension> extensions) {
		final Set<Plugin> unidentifiablePlugins = getUnidentifiablePlugins(mvnProject);

		final Set<Plugin> extPlugins = new LinkedHashSet<>();
		for (Extension ext : extensions) {
			final Plugin plugin = ext.asPlugin(mvnProject);
			try {
				if (ext.locatePluginDescriptor(this.mavenPluginManager, mvnSession.getRepositorySession(),
						mvnProject)) {
					extPlugins.add(plugin);
				}
			} catch (InvalidPluginDescriptorException | PluginDescriptorParsingException ignored) {
				unidentifiablePlugins.add(plugin);
			}
		}
		return extPlugins;
	}

	protected static Collection<Extension> scanCoreExtensions(final Collection<ClassRealm> realms) {
		final Collection<Extension> extensions = new LinkedHashSet<>();
		for (ClassRealm realm : realms) {
			Extension ext = fromExtRealm(realm);
			if (ext == null)
				continue;
			extensions.add(ext);
		}
		return Collections.unmodifiableCollection(extensions);
	}

	protected static Extension fromExtRealm(ClassRealm realm) {
		String id = realm.getId();
		if (id.startsWith(REALM_ID_CORE_EXT_PREFIX)) {
			id = id.substring(14);
		} else if (id.startsWith(REALM_ID_BUILD_EXT_PREFIX)) {
			id = id.substring(10);
		} else
			return null;
		final DefaultExtension ext = new DefaultExtension();
		ext.setClassRealm(realm);
		int idx = id.indexOf(':');
		ext.setGroupId(id.substring(0, idx));
		id = id.substring(idx + 1);
		idx = id.indexOf(':');
		ext.setArtifactId(id.substring(0, idx));
		ext.setVersion(id.substring(idx + 1));
		return ext;
	}

}
