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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.DuplicateRealmException;
import org.codehaus.plexus.classworlds.realm.NoSuchRealmException;
import net.runeduniverse.lib.utils.maven.ext.api.ExtensionIndex;
import net.runeduniverse.lib.utils.maven.ext.data.UnmodifiableExtensionData;
import net.runeduniverse.lib.utils.maven.ext.data.UnmodifiablePluginData;
import net.runeduniverse.lib.utils.maven.ext.data.api.Extension;
import net.runeduniverse.lib.utils.maven.ext.data.api.ExtensionData;
import net.runeduniverse.lib.utils.maven.ext.data.api.PluginData;

public class MvnCorePatcher {

	public static final String ERR_FAILED_TO_LOAD_MAVEN_EXTENSION_CLASSREALM = //
			"Failed to load maven-extension ClassRealm";

	public static final String REALM_ID_PLEXUS_CORE = "plexus.core";
	public static final String REALM_ID_MAVEN_EXT = "maven.ext";
	public static final String REALM_ID_CORE_EXT_PREFIX = "coreExtension>";
	public static final String REALM_ID_BUILD_EXT_PREFIX = "extension>";

	protected final ExtensionIndex extensionIndex;

	protected boolean coreExtension = false;
	// supplier
	protected ClassRealmFactory extensionRealmFactory = null;
	// events
	protected InfoEvent eventI_PatchingStarted = null;
	protected InfoEvent eventI_InvalidBuildExtension = null;
	protected InfoEvent eventI_SwitchRealmToPlexusCore = null;
	protected InfoEvent eventI_SwitchRealmToMavenExt = null;
	protected InfoEvent eventI_SwitchRealmToBuildExt = null;
	protected InfoEvent_Extensions eventI_ExtensionsDetected = null;
	protected InfoEvent_Plugins eventI_InvalidPluginsDetected = null;
	protected ErrorEvent eventE_PatchingAborted = null;
	protected InfoEvent eventI_ResetRealm = null;
	protected InfoEvent eventI_PatchingFinished = null;

	public MvnCorePatcher(final ExtensionIndex extensionIndex) {
		Objects.nonNull(extensionIndex);
		this.extensionIndex = extensionIndex;
	}

	public void markAsCoreExtension() {
		this.coreExtension = true;
	}

	public void withExtensionRealmFactory(final ClassRealmFactory extensionRealmFactory) {
		this.extensionRealmFactory = extensionRealmFactory;
	}

	protected ClassRealm createExtensionRealm(final ClassRealm plexusCore, final ClassRealm currentRealm)
			throws DuplicateRealmException {
		if (this.extensionRealmFactory == null)
			return null;
		return this.extensionRealmFactory.create(plexusCore, currentRealm);
	}

	protected boolean checkInvalidPlugins(final Map<MavenProject, Set<Plugin>> invalidPlugins) {
		for (Entry<MavenProject, Set<Plugin>> entry : invalidPlugins.entrySet()) {
			final Set<Plugin> set = entry.getValue();
			if (set != null && !set.isEmpty())
				return true;
		}
		return false;
	}

	protected Set<Plugin> getInvalidPlugins(final MavenProject mvnProject) {
		return this.extensionIndex.getInvalidPlugins()
				.get(mvnProject);
	}

	public boolean patchMaven(final MavenSession mvnSession, final Patch patch) throws MavenExecutionException {
		Objects.nonNull(mvnSession);
		if (patch == null)
			return false;

		callInfo_PatchingStarted();

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
				callInfo_SwitchRealmToPlexusCore();
				realm = currentRealm;
				extLoadState = 2;
			} else if (this.coreExtension) {
				callInfo_SwitchRealmToMavenExt();
				realm = world.getRealm(REALM_ID_MAVEN_EXT);
				extLoadState = 1;
			} else {
				realm = createExtensionRealm(plexusCore, currentRealm);
				if (realm == null) {
					callInfo_InvalidBuildExtension();
					return false;
				}
				callInfo_SwitchRealmToBuildExt();
				extLoadState = 0;
			}

			Thread.currentThread()
					.setContextClassLoader(realm);

			this.extensionIndex.discoverExtensions();

			for (MavenProject mvnProject : mvnSession.getAllProjects()) {
				this.extensionIndex.seedExtensions(mvnProject);
				this.extensionIndex.discoverPlugins(mvnSession.getRepositorySession(), mvnProject);
			}

			final Map<MavenProject, Set<Extension>> extensions = this.extensionIndex.getExtensions();
			final Map<MavenProject, Set<Plugin>> extPlugins = this.extensionIndex.getExtPlugins();

			if (!extensions.isEmpty()) {
				callInfo_ExtensionsDetected(mvnSession.getAllProjects(), extensions);
			}

			success = patch.apply(mvnSession, 0 < extLoadState, extensions, extPlugins);

		} catch (DuplicateRealmException | NoSuchRealmException e) {
			final MavenExecutionException ex = new MavenExecutionException(
					ERR_FAILED_TO_LOAD_MAVEN_EXTENSION_CLASSREALM, e);
			callError_PatchingAborted(ex);
			throw ex;
		} catch (MavenExecutionException ex) {
			callError_PatchingAborted(ex);
			throw ex;
		} finally {
			callInfo_ResetRealm();
			Thread.currentThread()
					.setContextClassLoader(currentRealm);
		}

		final Map<MavenProject, Set<Plugin>> invalidPlugins = this.extensionIndex.getInvalidPlugins();
		if (checkInvalidPlugins(invalidPlugins)) {
			callInfo_InvalidPluginsDetected(mvnSession.getAllProjects(), invalidPlugins);
		}

		callInfo_PatchingFinished();
		return success;
	}

	public static interface ClassRealmFactory {
		public ClassRealm create(final ClassRealm plexusCore, final ClassRealm currentRealm)
				throws DuplicateRealmException;
	}

	public static interface Patch {
		public boolean apply(final MavenSession mvnSession, final boolean isCoreExt,
				final Map<MavenProject, Set<Extension>> extensions, final Map<MavenProject, Set<Plugin>> extPlugins)
				throws MavenExecutionException;
	}

	// ------------------------------------------------------------------------
	// set Event Hooks
	// ------------------------------------------------------------------------

	public void onInfo_PatchingStarted(final InfoEvent event) {
		this.eventI_PatchingStarted = event;
	}

	public void onInfo_InvalidBuildExtension(final InfoEvent event) {
		this.eventI_InvalidBuildExtension = event;
	}

	public void onInfo_SwitchRealmToPlexus(final InfoEvent event) {
		this.eventI_SwitchRealmToPlexusCore = event;
	}

	public void onInfo_SwitchRealmToMavenExt(final InfoEvent event) {
		this.eventI_SwitchRealmToMavenExt = event;
	}

	public void onInfo_SwitchRealmToBuildExt(final InfoEvent event) {
		this.eventI_SwitchRealmToBuildExt = event;
	}

	public void onInfo_ExtensionsDetected(final InfoEvent_Extensions event) {
		this.eventI_ExtensionsDetected = event;
	}

	public void onInfo_InvalidPluginsDetected(final InfoEvent_Plugins event) {
		this.eventI_InvalidPluginsDetected = event;
	}

	public void onError_PatchingAborted(final ErrorEvent event) {
		this.eventE_PatchingAborted = event;
	}

	public void onInfo_ResetRealm(final InfoEvent event) {
		this.eventI_ResetRealm = event;
	}

	public void onInfo_PatchingFinished(final InfoEvent event) {
		this.eventI_PatchingFinished = event;
	}

	// ------------------------------------------------------------------------
	// call Events
	// ------------------------------------------------------------------------

	protected void callInfo_PatchingStarted() {
		callInfoEvent(this.eventI_PatchingStarted);
	}

	protected void callInfo_InvalidBuildExtension() {
		callInfoEvent(this.eventI_InvalidBuildExtension);
	}

	protected void callInfo_SwitchRealmToPlexusCore() {
		callInfoEvent(this.eventI_SwitchRealmToPlexusCore);
	}

	protected void callInfo_SwitchRealmToMavenExt() {
		callInfoEvent(this.eventI_SwitchRealmToMavenExt);
	}

	protected void callInfo_SwitchRealmToBuildExt() {
		callInfoEvent(this.eventI_SwitchRealmToBuildExt);
	}

	protected void callInfo_ExtensionsDetected(final Collection<MavenProject> projects,
			final Map<MavenProject, Set<Extension>> extensions) {
		callInfoEvent(this.eventI_ExtensionsDetected, projects, extensions);
	}

	protected void callInfo_InvalidPluginsDetected(final Collection<MavenProject> projects,
			final Map<MavenProject, Set<Plugin>> invalidPlugins) {
		callInfoEvent(this.eventI_InvalidPluginsDetected, projects, invalidPlugins);
	}

	protected void callError_PatchingAborted(final MavenExecutionException ex) {
		callErrorEvent(this.eventE_PatchingAborted, ex);
	}

	protected void callInfo_ResetRealm() {
		callInfoEvent(this.eventI_ResetRealm);
	}

	protected void callInfo_PatchingFinished() {
		callInfoEvent(this.eventI_PatchingFinished);
	}

	protected void callInfoEvent(final InfoEvent event) {
		if (event == null)
			return;
		event.call();
	}

	protected void callInfoEvent(final InfoEvent_Extensions event, final Collection<MavenProject> allProjects,
			final Map<MavenProject, Set<Extension>> extensions) {
		if (event == null)
			return;
		event.call(allProjects, new UnmodifiableExtensionData(extensions));
	}

	protected void callInfoEvent(final InfoEvent_Plugins event, final Collection<MavenProject> allProjects,
			final Map<MavenProject, Set<Plugin>> plugins) {
		if (event == null)
			return;
		event.call(allProjects, new UnmodifiablePluginData(plugins));
	}

	protected void callErrorEvent(final ErrorEvent event, final MavenExecutionException ex) {
		if (event == null)
			return;
		event.call(ex);
	}

	@FunctionalInterface
	public static interface InfoEvent {
		public void call();
	}

	@FunctionalInterface
	public static interface InfoEvent_Extensions {
		public void call(final Collection<MavenProject> allProjects, final ExtensionData data);
	}

	@FunctionalInterface
	public static interface InfoEvent_Plugins {
		public void call(final Collection<MavenProject> allProjects, final PluginData data);
	}

	@FunctionalInterface
	public static interface ErrorEvent {
		public void call(final MavenExecutionException ex);
	}
}
