package net.runeduniverse.lib.utils.scanner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.Data;

@Data
public class MethodPattern {
	private final Method method;

	public MethodPattern(Method method) {
		this.method = method;
		this.method.setAccessible(true);
	}

	public boolean invoke(Object obj, Object... args) {
		try {
			method.invoke(obj, args);
			return true;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return false;
		}
	}
}
