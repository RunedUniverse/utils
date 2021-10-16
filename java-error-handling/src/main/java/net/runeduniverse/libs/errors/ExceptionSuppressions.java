package net.runeduniverse.libs.errors;

import java.util.Collection;

public class ExceptionSuppressions extends ATrunkableException {
	private static final long serialVersionUID = 215607527815606000L;

	public ExceptionSuppressions(String message) {
		super(message);
	}

	public ExceptionSuppressions(String message, boolean trunk) {
		super(message, trunk);
	}

	public ExceptionSuppressions addSuppressed(Collection<Exception> errors) {
		for (Exception e : errors)
			this.addSuppressed(e);
		return this;
	}

	public boolean hasSuppressions() {
		return this.getSuppressed().length > 0;
	}
}
