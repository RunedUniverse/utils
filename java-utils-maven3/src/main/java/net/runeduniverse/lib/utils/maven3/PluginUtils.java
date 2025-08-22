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
package net.runeduniverse.lib.utils.maven3;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.logging.Log;

import net.runeduniverse.lib.utils.maven3.api.MavenProperties;

public class PluginUtils {

	private PluginUtils() {
	}

	public static <T extends Mojo> String getVersionFromArtifact(final Class<T> clazz, final Log log,
			final String groupId, final String artifactId) {
		final Properties p = new Properties();
		try (final InputStream stream = clazz.getClassLoader()
				.getResourceAsStream(
						String.format(MavenProperties.METAINF.MAVEN.TMP_POM_PROPERTIES, groupId, artifactId))) {
			if (stream != null) {
				p.load(stream);
			}
		} catch (IOException | IllegalArgumentException ignored) {
			if (log.isDebugEnabled())
				log.error("Failed to load pom.properties from Mojo source!", ignored);
		}
		return p.getProperty("version");
	}

}
