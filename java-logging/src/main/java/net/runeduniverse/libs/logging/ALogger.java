package net.runeduniverse.libs.logging;

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
