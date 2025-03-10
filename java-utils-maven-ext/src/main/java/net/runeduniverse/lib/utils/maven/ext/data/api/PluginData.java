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
package net.runeduniverse.lib.utils.maven.ext.data.api;

import java.util.Map;
import java.util.Set;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;

public interface PluginData {
	public Set<Plugin> getPlugins();

	public Set<Plugin> getPlugins(final MavenProject mvnProject);

	public Map<MavenProject, Set<Plugin>> getPluginMap();
}