package net.runeduniverse.lib.utils.scanner.debug;

import java.net.URL;

import lombok.Getter;
import net.runeduniverse.lib.utils.logging.logs.CompoundTree;

@Getter
public class IntercepterSection implements IIntercepter {

	private final String id;
	private final CompoundTree tree;

	public IntercepterSection(String id, String headline) {
		this.id = id;
		this.tree = new CompoundTree(id, headline);
	}

	public URL intercept(URL url) {
		this.tree.append("URL", url.toString());
		return url;
	}

	public String intercept(String s) {
		this.tree.append(s);
		return s;
	}
}
