package net.runeduniverse.libs.scanner.debug;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Intercepter {
	private final List<StringListBuilder> builder = new ArrayList<>();
	private final boolean active;
	private StringListBuilder activeBuilder;

	public Intercepter(String headline, boolean active) {
		this.activeBuilder = new StringListBuilder(headline);
		this.builder.add(this.activeBuilder);
		this.active = active;
	}

	public Intercepter addSection(String headline) {
		StringListBuilder b = new StringListBuilder(headline);
		this.activeBuilder = b;
		this.builder.add(b);
		return this;
	}

	public void print() {
		if (this.active)
			for (StringListBuilder b : builder)
				System.out.println(b);
	}

	public URL intercept(URL url) {
		if (this.active)
			this.activeBuilder.appendElement(url.toString());
		return url;
	}

	public String intercept(String s) {
		if (this.active)
			this.activeBuilder.appendElement(s);
		return s;
	}
}
