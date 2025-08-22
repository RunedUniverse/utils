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
package net.runeduniverse.lib.utils.logging;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public abstract class ALogger extends Logger {

	private final DebugLogger debugLogger;

	protected ALogger(String name, String resourceBundleName) {
		super(name, resourceBundleName);
		this.debugLogger = null;
	}

	protected ALogger(String name, String resourceBundleName, Logger parent) {
		super(name, resourceBundleName);
		if (parent == null) {
			parent = LogManager.getLogManager()
					.getLogger(Logger.GLOBAL_LOGGER_NAME);
			parent.setLevel(Level.ALL);
		}
		super.setParent(parent);
		this.debugLogger = parent instanceof DebugLogger ? (DebugLogger) parent : null;
	}

	@Override
	public void log(LogRecord record) {
		if (this.debugLogger == null)
			super.log(record);
		else
			this.debugLogger.log(record);
	}
}
