/*
 * Copyright © 2021 Pl4yingNight (pl4yingnight@gmail.com)
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
package net.runeduniverse.lib.utils.logging.test;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class DebugLogger extends Logger {

	public DebugLogger() {
		super("ROGM-DEBUG", null);
		this.setLevel(Level.ALL);
	}

	public DebugLogger(Logger parent) {
		this();
		super.setParent(parent);
	}

	@Override
	public void log(LogRecord record) {
		if (!isLoggable(record.getLevel())) {
			record.setMessage("[TRACING-OVERRIDE][" + record.getLevel()
					.getName() + "]\n" + record.getMessage());
			record.setLevel(Level.INFO);
		}
		super.log(record);
	}

	private static final int offValue = Level.OFF.intValue();
	private static final int infoLevelValue = Level.INFO.intValue();

	public boolean isLoggable(Level level) {
		if (level.intValue() < infoLevelValue || infoLevelValue == offValue) {
			return false;
		}
		return true;
	}
}
