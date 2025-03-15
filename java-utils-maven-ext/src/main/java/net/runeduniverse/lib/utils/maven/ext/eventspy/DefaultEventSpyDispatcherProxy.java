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
package net.runeduniverse.lib.utils.maven.ext.eventspy;

import org.apache.maven.eventspy.internal.EventSpyDispatcher;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import net.runeduniverse.lib.utils.maven.ext.eventspy.api.EventSpyDispatcherProxy;

@Component(role = EventSpyDispatcherProxy.class, instantiationStrategy = "keep-alive")
/**
 * This class provides a proxy to dispatch events. This Proxy has become
 * nessasary as maven at some point stopped providing it's internal
 * {@code EventSpyDispatcher} to build-extensions without providing an
 * alternative! <br>
 * I expect this change to have accoured some time after Maven version '3.3.9'
 *
 * @author VenaNocta
 */
public class DefaultEventSpyDispatcherProxy implements EventSpyDispatcherProxy, Initializable {

	@Requirement
	protected Logger log;
	@Requirement
	private PlexusContainer container;

	protected DispatcherProxy proxy = null;

	@Override
	public void initialize() throws InitializationException {
		synchronized (this) {
			final ClassRealm currentRealm = (ClassRealm) Thread.currentThread()
					.getContextClassLoader();
			try {
				locateDispatcherProxy(currentRealm);
			} catch (ClassNotFoundException | ComponentLookupException ignored) {
			}
			if (this.proxy == null && this.log.isDebugEnabled()) {
				this.log.fatalError(
						"This Maven version does not provide an EventSpyDispatcher class to build-extensions!");
				this.log.fatalError(
						"Be adviced that build-extensions are unable to dispatch or receive events at this point!");
			}
		}
	}

	@Override
	public boolean getFeatureState() {
		synchronized (this) {
			return this.proxy != null;
		}
	}

	protected DispatcherProxy getProxy() {
		synchronized (this) {
			return this.proxy;
		}
	}

	@Override
	public void onEvent(Object event) {
		final DispatcherProxy proxy = getProxy();
		if (proxy != null)
			proxy.onEvent(event);
	}

	public void locateDispatcherProxy(final ClassRealm realm) throws ClassNotFoundException, ComponentLookupException {
		synchronized (this.container) {
			synchronized (this) {
				final ClassRealm oldLookupRealm = this.container.setLookupRealm(realm);
				final ClassLoader oldClassLoader = Thread.currentThread()
						.getContextClassLoader();
				Thread.currentThread()
						.setContextClassLoader(realm);
				try {
					// validate that dependency is loadable
					realm.loadClass("org.apache.maven.eventspy.internal.EventSpyDispatcher");
					// if it did not already fail, than try to load the proxy
					this.proxy = this.container.lookup(DispatcherProxy.class, "v3.0.2");
				} finally {
					Thread.currentThread()
							.setContextClassLoader(oldClassLoader);
					this.container.setLookupRealm(oldLookupRealm);
				}
			}
		}
	}

	public static interface DispatcherProxy {
		public void onEvent(Object event);
	}

	@Component(role = DispatcherProxy.class, hint = "v3.0.2")
	public static class DispatcherProxy_v3_0_2 implements DispatcherProxy {
		@Requirement
		protected EventSpyDispatcher dispatcher;

		@Override
		public void onEvent(Object event) {
			this.dispatcher.onEvent(event);
		}
	}
}
