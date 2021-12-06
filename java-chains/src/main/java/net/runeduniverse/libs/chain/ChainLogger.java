package net.runeduniverse.libs.chain;

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

	public void logTrace(ChainRuntimeExecutionTrace trace) {
		super.log(Level.FINE, trace.toString());
	}
}
