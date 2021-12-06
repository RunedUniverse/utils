package net.runeduniverse.lib.utils.scanner.debug;

import java.net.URL;

public interface IIntercepter {

	public URL intercept(URL url);

	public String intercept(String s);
}
