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
package net.runeduniverse.lib.utils.chain;

import java.util.logging.Logger;

public class ChainLogger extends Logger {

	public ChainLogger(Logger parent) {
		super("ChainLogger", null);
		super.setParent(parent);
	}

	public <E extends Exception> E throwing(Class<?> clazz, String sourceMethod, E thrown) {
		super.throwing(clazz.getName(), sourceMethod, thrown);
		return thrown;
	}

	public void burying(String sourceMethod, Exception exception) {
		super.log(Level.BURY, sourceMethod + "\n" + exception);
	}

	public void logTrace(DefaultChainRuntimeExecutionTrace trace) {
		super.log(Level.FINE, trace.toString());
	}
}
