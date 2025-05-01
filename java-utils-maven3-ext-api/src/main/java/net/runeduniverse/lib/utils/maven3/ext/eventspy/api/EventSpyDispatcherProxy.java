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
package net.runeduniverse.lib.utils.maven3.ext.eventspy.api;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

public interface EventSpyDispatcherProxy {

	/**
	 * Check the state of the maven wide events for this instance
	 *
	 * @return {@code true} if this proxy can dispatch events through maven's event
	 *         dispatcher ({@code EventSpyDispatcher} else {@code false}
	 */
	public boolean getFeatureState();

	public void onEvent(Object event);

	public void locateDispatcherProxy(final ClassRealm realm) throws ComponentLookupException;

}
