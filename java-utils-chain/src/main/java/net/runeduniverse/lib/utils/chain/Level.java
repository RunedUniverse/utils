package net.runeduniverse.lib.utils.chain;

public class Level extends java.util.logging.Level {
	private static final long serialVersionUID = -1369172045174361996L;

	protected Level(String name, int value) {
		super(name, value);
	}

	public static final Level BURY = new Level("BURY", 601);

}
