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
package net.runeduniverse.lib.utils.logging;

import java.util.logging.Logger;

public class UniversalLogger extends ALogger {

	private final Class<?> clazz;

	public UniversalLogger(final Class<?> clazz, final Logger parent) {
		super(clazz.getSimpleName(), null, parent);
		this.clazz = clazz;
	}

	public <E extends Exception> E throwing(final String sourceMethod, final E thrown) {
		super.throwing(clazz.getName(), sourceMethod, thrown);
		return thrown;
	}

	public void burying(final String sourceMethod, final Exception exception) {
		super.log(Level.BURY, sourceMethod + "\n" + exception);
	}
}
