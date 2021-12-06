package net.runeduniverse.libs.utils;

public class StringUtils {

	public static boolean isBlank(final String s) {
		// Null-safe, short-circuit evaluation.
		return s == null || s.trim().isEmpty();
	}

}
