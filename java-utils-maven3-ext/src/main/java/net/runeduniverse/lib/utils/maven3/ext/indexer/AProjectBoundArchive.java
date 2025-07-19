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
package net.runeduniverse.lib.utils.maven3.ext.indexer;

import java.util.Collection;
import java.util.Map;
import org.apache.maven.project.MavenProject;
import net.runeduniverse.lib.utils.logging.log.DefaultCompoundTree;
import net.runeduniverse.lib.utils.logging.log.api.CompoundTree;
import net.runeduniverse.lib.utils.logging.log.api.Recordable;
import net.runeduniverse.lib.utils.maven3.ext.indexer.api.ProjectBoundRegistry;

public abstract class AProjectBoundArchive<S> extends AProjectBoundRegistry<S>
		implements ProjectBoundRegistry<S>, Recordable {

	protected AProjectBoundArchive() {
		super();
	}

	protected AProjectBoundArchive(final Map<MavenProject, S> registry) {
		super(registry);
	}

	@Override
	public S createSector(final MavenProject mvnProject) {
		final S sector = super.createSector(mvnProject);

		final MavenProject upstreamMvnProject = mvnProject.getParent();
		if (upstreamMvnProject != null)
			_updateSector(sector, this.sectors.get(upstreamMvnProject));

		S downstreamSector = null;
		final Collection<MavenProject> downstreamMvnProjects = mvnProject.getCollectedProjects();
		if (downstreamMvnProjects != null) {
			for (MavenProject downstreamMvnProject : downstreamMvnProjects) {
				downstreamSector = this.sectors.get(downstreamMvnProject);
				if (downstreamSector == null)
					continue;
				_updateSector(downstreamSector, sector);
			}
		}

		return sector;
	}

	protected void _updateSector(final S child, final S parent) {
	}

	@Override
	public CompoundTree toRecord() {
		final CompoundTree tree = new DefaultCompoundTree(_getRecordTitle());

		for (S sector : this.sectors.values()) {
			if (sector instanceof Recordable)
				tree.append(((Recordable) sector).toRecord());
		}

		return tree;
	}

	protected String _getRecordTitle() {
		return getClass().getCanonicalName();
	}
}
