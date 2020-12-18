package net.runeduniverse.libs.utils;

import java.util.Collection;

public class CollectionUtils {

	public static final <T> T first(Collection<T> collection) {
		for (T t : collection)
			return t;
		return null;
	}

	public static final <T> T firstNotNull(Collection<T> collection) {
		for (T t : collection)
			if (t != null)
				return t;
		return null;
	}
}
