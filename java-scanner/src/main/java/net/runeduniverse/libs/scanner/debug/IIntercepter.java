package net.runeduniverse.libs.scanner.debug;

import java.net.URL;

public interface IIntercepter {

	public URL intercept(URL url);

	public String intercept(String s);
}
