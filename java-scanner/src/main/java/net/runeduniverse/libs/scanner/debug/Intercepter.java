package net.runeduniverse.libs.scanner.debug;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Intercepter implements IIntercepter {
	private final boolean active;
	private IntercepterSection baseSection;
	private Map<String, IntercepterSection> sections = new HashMap<>();

	public Intercepter(String headline, boolean active) {
		this.baseSection = new IntercepterSection(null, headline);
		this.active = active;
	}

	public IIntercepter addSection(IntercepterSection section) {
		this.sections.put(section.getId(), section);
		return section;
	}

	public IIntercepter addSection(String id, String headline) {
		return this.addSection(new IntercepterSection(id, headline));
	}

	@Override
	public String toString() {
		Set<String> keys = this.sections.keySet();
		StringListBuilder resultBuilder = this.baseSection.getBuilder();
		for (String k : keys)
			resultBuilder.append(this.sections.get(k)
					.getBuilder());
		return resultBuilder.toString();
	}

	public void print() {
		if (!this.active)
			return;
		System.out.println(this.toString());
	}

	@Override
	public URL intercept(URL url) {
		return this.baseSection.intercept(url);
	}

	@Override
	public String intercept(String s) {
		return this.baseSection.intercept(s);
	}
}
