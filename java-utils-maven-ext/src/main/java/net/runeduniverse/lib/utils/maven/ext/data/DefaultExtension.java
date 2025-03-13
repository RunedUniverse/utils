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
package net.runeduniverse.lib.utils.maven.ext.data;

import java.security.CodeSource;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.InvalidPluginDescriptorException;
import org.apache.maven.plugin.MavenPluginManager;
import org.apache.maven.plugin.PluginDescriptorParsingException;
import org.apache.maven.plugin.PluginResolutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.utils.StringUtils;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.eclipse.aether.RepositorySystemSession;

import net.runeduniverse.lib.utils.maven.ext.data.api.Extension;

public class DefaultExtension implements Extension {

	protected ClassRealm realm = null;
	protected CodeSource source = null;
	protected String groupId = null;
	protected String artifactId = null;
	protected String version = null;

	protected Plugin pluginInstance = null;
	protected Map<MavenProject, Plugin> plugins = new LinkedHashMap<>(1);
	protected Map<MavenProject, PluginDescriptor> descriptors = new LinkedHashMap<>(1);

	@Override
	public ClassRealm getClassRealm() {
		return this.realm;
	}

	@Override
	public CodeSource getCodeSource() {
		return this.source;
	}

	@Override
	public String getGroupId() {
		return this.groupId;
	}

	@Override
	public String getArtifactId() {
		return this.artifactId;
	}

	@Override
	public String getVersion() {
		return this.version;
	}

	@Override
	public String getId() {
		final StringBuilder builder = new StringBuilder(String.format("%s:%s", getGroupId(), getArtifactId()));
		final String version = getVersion();
		if (!StringUtils.isBlank(version))
			builder.append(String.format(":%s", version));
		return builder.toString();
	}

	@Override
	public boolean isPlugin(final MavenProject mvnProject) {
		return this.descriptors.get(mvnProject) != null;
	}

	@Override
	public PluginDescriptor getPluginDescriptor(final MavenProject mvnProject) {
		return this.descriptors.get(mvnProject);
	}

	@Override
	public void setClassRealm(final ClassRealm realm) {
		this.realm = realm;
	}

	@Override
	public void setCodeSource(CodeSource source) {
		this.source = source;
	}

	@Override
	public void setGroupId(String groupId) {
		if (groupId != null)
			groupId = groupId.trim();
		this.groupId = groupId;
	}

	@Override
	public void setArtifactId(String artifactId) {
		if (artifactId != null)
			artifactId = artifactId.trim();
		this.artifactId = artifactId;
	}

	@Override
	public void setVersion(String version) {
		if (version != null)
			version = version.trim();
		this.version = version;
	}

	@Override
	public void setPlugin(final Plugin plugin) {
		this.pluginInstance = plugin;
	}

	@Override
	public void setPlugin(final MavenProject mvnProject, final Plugin plugin) {
		this.plugins.put(mvnProject, plugin);
	}

	@Override
	public void setPluginDescriptor(final MavenProject mvnProject, final PluginDescriptor descriptor) {
		this.descriptors.put(mvnProject, descriptor);
		if (descriptor == null)
			return;
		if (descriptor.getPlugin() == null) {
			descriptor.setPlugin(asPlugin(mvnProject));
		} else {
			setPlugin(mvnProject, descriptor.getPlugin());
		}
	}

	protected Plugin createPlugin() {
		final Plugin plugin = new Plugin();
		plugin.setExtensions(true);
		plugin.setGroupId(this.groupId);
		plugin.setArtifactId(this.artifactId);
		plugin.setVersion(this.version);
		return plugin;
	}

	@Override
	public Plugin asPlugin() {
		if (this.pluginInstance == null) {
			this.pluginInstance = createPlugin();
		}
		return this.pluginInstance;
	}

	@Override
	public Plugin asPlugin(final MavenProject mvnProject) {
		return this.plugins.computeIfAbsent(mvnProject, k -> asPlugin());
	}

	@Override
	public boolean validate() {
		boolean valid = true;
		if (this.groupId == null || this.groupId.length() == 0)
			valid = false;
		if (this.artifactId == null || this.artifactId.length() == 0)
			valid = false;
		if (this.version == null || this.version.length() == 0)
			valid = false;
		return valid;
	}

	@Override
	public boolean locatePluginDescriptor(final MavenPluginManager manager, final RepositorySystemSession session,
			final MavenProject mvnProject) throws InvalidPluginDescriptorException, PluginDescriptorParsingException {
		try {
			setPluginDescriptor(mvnProject, manager.getPluginDescriptor(asPlugin(mvnProject),
					mvnProject.getRemotePluginRepositories(), session));
			return true;
		} catch (PluginResolutionException e) {
			return false;
		}
	}
}
