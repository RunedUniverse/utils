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
package net.runeduniverse.lib.utils.maven.ext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.InvalidPluginDescriptorException;
import org.apache.maven.plugin.MavenPluginManager;
import org.apache.maven.plugin.PluginDescriptorParsingException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.logging.Logger;
import org.eclipse.aether.RepositorySystemSession;

import net.runeduniverse.lib.utils.maven.ext.api.ExtensionIndex;
import net.runeduniverse.lib.utils.maven.ext.data.DefaultExtension;
import net.runeduniverse.lib.utils.maven.ext.data.api.Extension;
import net.runeduniverse.lib.utils.plexus.PlexusContextUtils;

import static net.runeduniverse.lib.utils.common.CollectionUtils.copy;
import static net.runeduniverse.lib.utils.common.CollectionUtils.unmodifiable;

@Component(role = ExtensionIndex.class)
public class DefaultExtensionIndex implements ExtensionIndex {

	public static final String REALM_ID_PLEXUS_CORE = "plexus.core";
	public static final String REALM_ID_MAVEN_EXT = "maven.ext";
	public static final String REALM_ID_CORE_EXT_PREFIX = "coreExtension>";
	public static final String REALM_ID_BUILD_EXT_PREFIX = "extension>";
	public static final String POM_PROP_FILE = "/META-INF/maven/*/*/pom.properties";

	// supplier
	protected final Supplier<Map<MavenProject, Set<Extension>>> extMapSupplier;
	protected final Supplier<Map<MavenProject, Set<Plugin>>> pluginMapSupplier;
	protected final Supplier<Set<Extension>> extSetSupplier;
	protected final Supplier<Set<Plugin>> pluginSetSupplier;

	// caches
	protected final Set<Extension> coreExtensions;
	protected final Map<MavenProject, Set<Extension>> extensionMap;
	protected final Map<MavenProject, Set<Plugin>> extPlugins;
	// index
	protected final Map<ClassRealm, Set<Extension>> realmMap;
	protected final Map<ClassRealm, Set<CodeSource>> extSourcesByRealm;
	protected final Map<MavenProject, Set<Plugin>> invalidPlugins;

	@Requirement
	private Logger log;
	@Requirement
	protected PlexusContainer container;
	@Requirement
	protected MavenPluginManager mavenPluginManager;

	public DefaultExtensionIndex() {
		this(ConcurrentHashMap::new, () -> ConcurrentHashMap.newKeySet(), //
				ConcurrentHashMap::new, () -> ConcurrentHashMap.newKeySet());
	}

	public DefaultExtensionIndex(final Supplier<Map<MavenProject, Set<Extension>>> extMapSupplier,
			final Supplier<Set<Extension>> extSetSupplier,
			final Supplier<Map<MavenProject, Set<Plugin>>> pluginMapSupplier,
			final Supplier<Set<Plugin>> pluginSetSupplier) {
		// supplier
		this.extMapSupplier = extMapSupplier;
		this.pluginMapSupplier = pluginMapSupplier;
		this.extSetSupplier = extSetSupplier;
		this.pluginSetSupplier = pluginSetSupplier;
		// caches
		this.coreExtensions = this.extSetSupplier.get();
		this.extensionMap = this.extMapSupplier.get();
		this.extPlugins = this.pluginMapSupplier.get();
		// index
		this.realmMap = new ConcurrentHashMap<>();
		this.extSourcesByRealm = new ConcurrentHashMap<>();
		this.invalidPlugins = new ConcurrentHashMap<>();
	}

	@Override
	public void discoverExtensions() {
		final ClassRealm currentRealm = (ClassRealm) Thread.currentThread()
				.getContextClassLoader();
		final ClassWorld world = currentRealm.getWorld();

		ClassRealm searchRealm = world.getClassRealm(REALM_ID_MAVEN_EXT);
		if (searchRealm == null)
			searchRealm = world.getClassRealm(REALM_ID_PLEXUS_CORE);

		// collect all extension realms
		final Set<ClassRealm> coreRealms = new LinkedHashSet<>();
		final Set<ClassRealm> realms = new LinkedHashSet<>();

		this.log.debug("DefaultExtensionIndex : Scanning LifecycleParticipants");
		for (ComponentDescriptor<AbstractMavenLifecycleParticipant> descriptor : PlexusContextUtils
				.getPlexusComponentDescriptorList(this.container, searchRealm, AbstractMavenLifecycleParticipant.class,
						null)) {
			this.log.debug("- " + descriptor);
			final Class<?> clazz = descriptor.getImplementationClass();
			ClassRealm realm = descriptor.getRealm();
			if (realm == null) {
				final ClassLoader loader = clazz.getClassLoader();
				if (loader instanceof ClassRealm) {
					realm = (ClassRealm) loader;
					this.log.debug("  └ located ClassRealm: " + realm);
				}
			}
			if (realm != null) {
				final String realmId = realm.getId();
				realms.add(realm);
				if (realmId != null && realmId.startsWith(REALM_ID_CORE_EXT_PREFIX))
					coreRealms.add(realm);
			}

			CodeSource source = null;
			try {
				source = clazz.getProtectionDomain()
						.getCodeSource();
			} catch (SecurityException ignored) {
				if (this.log.isDebugEnabled())
					this.log.warn("Failed to load LifecycleParticipant source!", ignored);
			}

			if (source == null) {
				if (this.log.isDebugEnabled())
					this.log.warn("Found LifecycleParticipant without CodeSource!");
				continue;
			}

			if (realm != null)
				this.extSourcesByRealm.computeIfAbsent(realm, k -> ConcurrentHashMap.newKeySet())
						.add(source);
		}

		for (ClassRealm realm : world.getRealms()) {
			if (realms.contains(realm))
				continue;
			final String realmId = realm.getId();
			if (realmId == null)
				continue;
			if (realmId.startsWith(REALM_ID_CORE_EXT_PREFIX)) {
				coreRealms.add(realm);
			} else if (!realmId.startsWith(REALM_ID_BUILD_EXT_PREFIX))
				continue;
			realms.add(realm);
		}

		// parse the extension references
		this.log.debug("DefaultExtensionIndex : Locating Extensions from Realms");
		for (ClassRealm realm : realms) {
			this.log.debug("> " + realm.getId());
			final Set<Extension> set = this.realmMap.computeIfAbsent(realm, this::fromExtRealm);
			boolean inCore = coreRealms.contains(realm);
			for (Extension ext : set) {
				this.log.debug("  - " + ext.getId());
				if (inCore)
					this.coreExtensions.add(ext);
			}
		}
	}

	@Override
	public boolean discoverExtRealm(final ClassRealm realm) {
		final Set<Extension> set = this.realmMap.computeIfAbsent(realm, this::fromExtRealm);
		return !set.isEmpty();
	}

	protected Set<Extension> fromExtRealm(final ClassRealm realm) {
		final String realmId = realm.getId();
		final Set<Extension> set = this.extSetSupplier.get();

		final Set<CodeSource> sources = this.extSourcesByRealm.get(realm);
		boolean srcFailed = sources == null || sources.isEmpty();
		if (!srcFailed && 1 < sources.size()) {
			for (CodeSource source : sources) {
				// load ID info from jar file
				final Extension ext = new DefaultExtension();
				ext.setClassRealm(realm);
				parseIdFromCodeSource(ext, source);
				set.add(ext);
			}
			if (!set.isEmpty())
				return set;
			srcFailed = true;
		}

		if (realmId == null)
			return set;

		final Extension ext = new DefaultExtension();
		ext.setClassRealm(realm);
		if (realmId.startsWith(REALM_ID_CORE_EXT_PREFIX)) {
			this.coreExtensions.add(ext);
			parseIdFromRealmId(ext, REALM_ID_CORE_EXT_PREFIX, realmId);
		} else if (realmId.startsWith(REALM_ID_BUILD_EXT_PREFIX)) {
			parseIdFromRealmId(ext, REALM_ID_BUILD_EXT_PREFIX, realmId);
		} else
			return set;

		ext.setClassRealm(realm);
		set.add(ext);

		if (!ext.validate()) {
			if (srcFailed)
				return set;
			// load ID info from jar file
			parseIdFromCodeSource(ext, sources.iterator()
					.next());
		}
		return set;
	}

	protected void parseIdFromRealmId(final Extension ext, final String prefix, String id) {
		if (id.startsWith(prefix)) {
			id = id.substring(prefix.length());
		} else
			return;
		int idx = id.indexOf(':');
		if (idx == -1)
			return;
		ext.setGroupId(id.substring(0, idx));
		id = id.substring(idx + 1);
		idx = id.indexOf(':');
		if (idx == -1)
			return;
		ext.setArtifactId(id.substring(0, idx));
		ext.setVersion(id.substring(idx + 1));
	}

	protected boolean parseIdFromCodeSource(final Extension ext, final CodeSource source) {
		if (source == null)
			return false;
		final Properties p = new Properties();
		try (final URLClassLoader loader = new URLClassLoader(new URL[] { source.getLocation() })) {
			try (final InputStream stream = loader.getResourceAsStream(POM_PROP_FILE)) {
				if (stream == null)
					return false;
				p.load(stream);
			} catch (IOException | IllegalArgumentException ignored) {
				if (this.log.isDebugEnabled())
					this.log.warn("Failed to load pom.properties from LifecycleParticipant source!", ignored);
			}
		} catch (IOException ignored) {
			if (this.log.isDebugEnabled())
				this.log.debug(null, ignored);
		}
		if (p.isEmpty())
			return false;
		ext.setCodeSource(source);
		ext.setGroupId(p.getProperty("groupId"));
		ext.setArtifactId(p.getProperty("artifactId"));
		ext.setVersion(p.getProperty("version"));
		return true;
	}

	@Override
	public void seedExtensions(final MavenProject mvnProject) {
		final Set<Extension> extensions = this.extensionMap.computeIfAbsent(mvnProject, k -> this.extSetSupplier.get());
		extensions.addAll(this.coreExtensions);

		// scan build-extensions = only "extension>" realms should be directly imported
		for (ClassRealm realm : mvnProject.getClassRealm()
				.getImportRealms()) {
			final Set<Extension> set = this.realmMap.get(realm);
			if (set == null)
				continue;
			extensions.addAll(set);
		}
	}

	@Override
	public void discoverPlugins(final RepositorySystemSession repoSysSession, final MavenProject mvnProject) {
		discoverPlugins(repoSysSession, mvnProject, this.extensionMap.get(mvnProject));
	}

	protected void discoverPlugins(final RepositorySystemSession session, final MavenProject mvnProject,
			final Set<Extension> extensions) {
		if (extensions == null || extensions.isEmpty())
			return;
		final Set<Plugin> invalidPlugins = this.invalidPlugins.computeIfAbsent(mvnProject,
				k -> this.pluginSetSupplier.get());
		final Set<Plugin> extPlugins = this.extPlugins.computeIfAbsent(mvnProject, k -> this.pluginSetSupplier.get());

		for (Extension ext : extensions) {
			final Plugin plugin = ext.asPlugin(mvnProject);
			if (extPlugins.contains(plugin) || invalidPlugins.contains(plugin))
				continue;
			try {
				if (ext.locatePluginDescriptor(this.mavenPluginManager, session, mvnProject)) {
					extPlugins.add(plugin);
				}
			} catch (InvalidPluginDescriptorException | PluginDescriptorParsingException ignored) {
				if (this.log.isDebugEnabled()) {
					this.log.debug("Invalid Plugin detected!", ignored);
				}
				invalidPlugins.add(plugin);
			}
		}
		return;
	}

	public Set<Extension> getCoreExtensions() {
		return unmodifiable(this.coreExtensions);
	}

	public Map<MavenProject, Set<Extension>> getExtensions() {
		final Map<MavenProject, Set<Extension>> map = this.extMapSupplier.get();
		for (Entry<MavenProject, Set<Extension>> entry : this.extensionMap.entrySet()) {
			map.put(entry.getKey(), unmodifiable(copy(entry.getValue(), this.extSetSupplier)));
		}
		return unmodifiable(map);
	}

	public Map<MavenProject, Set<Plugin>> getExtPlugins() {
		final Map<MavenProject, Set<Plugin>> map = this.pluginMapSupplier.get();
		for (Entry<MavenProject, Set<Plugin>> entry : this.extPlugins.entrySet()) {
			map.put(entry.getKey(), unmodifiable(copy(entry.getValue(), this.pluginSetSupplier)));
		}
		return unmodifiable(map);
	}

	public Map<MavenProject, Set<Plugin>> getInvalidPlugins() {
		final Map<MavenProject, Set<Plugin>> map = this.pluginMapSupplier.get();
		for (Entry<MavenProject, Set<Plugin>> entry : this.invalidPlugins.entrySet()) {
			map.put(entry.getKey(), unmodifiable(copy(entry.getValue(), this.pluginSetSupplier)));
		}
		return unmodifiable(map);
	}
}
