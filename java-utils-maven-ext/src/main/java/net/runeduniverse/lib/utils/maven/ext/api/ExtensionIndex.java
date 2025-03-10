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
package net.runeduniverse.lib.utils.maven.ext.api;

import java.util.Map;
import java.util.Set;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.eclipse.aether.RepositorySystemSession;

import net.runeduniverse.lib.utils.maven.ext.data.api.Extension;

public interface ExtensionIndex {

	public void discoverExtensions();

	public boolean discoverExtRealm(final ClassRealm realm);

	public void seedExtensions(final MavenProject mvnProject);

	public void discoverPlugins(final RepositorySystemSession repoSysSession, final MavenProject mvnProject);

	public Set<Extension> getCoreExtensions();

	public Map<MavenProject, Set<Extension>> getExtensions();

	public Map<MavenProject, Set<Plugin>> getExtPlugins();

	public Map<MavenProject, Set<Plugin>> getInvalidPlugins();

}
