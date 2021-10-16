package net.runeduniverse.libs.errors;

import java.util.ArrayList;
import java.util.List;

public abstract class ATrunkableException extends Exception {
	private static final long serialVersionUID = -3060179675399411806L;

	public ATrunkableException(String message) {
		super(message);
	}

	public ATrunkableException(String message, Throwable cause) {
		super(message, cause);
	}

	public ATrunkableException(String message, boolean trunk) {
		super(ATrunkableException.addPrefix(message, trunk));
		if (trunk)
			this.trunk();
	}

	public ATrunkableException(String message, Throwable cause, boolean trunk) {
		super(ATrunkableException.addPrefix(message, trunk), cause);
		if (trunk)
			this.trunk();
	}

	private static String addPrefix(String message, boolean trunk) {
		return trunk ? "[TRUNKED] " + message : message;
	}

	protected void trunk() {
		List<StackTraceElement> trace = new ArrayList<>();
		for (StackTraceElement element : this.getStackTrace())
			trace.add(element);
		this.trunkStackTrace(trace);
		this.setStackTrace(trace.toArray(new StackTraceElement[trace.size()]));
		this.printStackTrace();
	}

	protected void trunkStackTrace(List<StackTraceElement> trace) {
		trace.removeIf(ATrunkableException::removeJUnitStackTrace);
	}

	protected static boolean removeJUnitStackTrace(StackTraceElement element) {
		return ATrunkableException.pathStartsWith(element, "org.junit.");
	}

	protected static boolean pathStartsWith(StackTraceElement element, String prefix) {
		return element.getClassName()
				.startsWith(prefix);
	}
}
