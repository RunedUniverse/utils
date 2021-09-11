package net.runeduniverse.libs.scanner.debug;

import java.net.URL;

import lombok.Getter;

@Getter
public class IntercepterSection implements IIntercepter {

	private final String id;
	private final StringListBuilder builder;

	public IntercepterSection(String id, String headline) {
		this.id = id;
		this.builder = new StringListBuilder(headline);
	}

	public IntercepterSection(String id, String headline, CharSequence lineIndent) {
		this.id = id;
		this.builder = new StringListBuilder(headline, lineIndent);
	}

	public URL intercept(URL url) {
		this.builder.appendElement(url.toString());
		return url;
	}

	public String intercept(String s) {
		this.builder.appendElement(s);
		return s;
	}
}
