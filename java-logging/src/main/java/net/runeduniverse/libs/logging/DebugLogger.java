package net.runeduniverse.libs.logging;

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
