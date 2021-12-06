package net.runeduniverse.lib.utils.chain.errors;

import net.runeduniverse.lib.utils.errors.ATrunkableException;

public class ChainLayerCallException extends ATrunkableException {
	private static final long serialVersionUID = -6315371891932847527L;

	public ChainLayerCallException(String message, Throwable cause) {
		super(message, cause, true);
	}
}
