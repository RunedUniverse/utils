package net.runeduniverse.libs.logging;

import org.junit.jupiter.api.Test;

import net.runeduniverse.libs.logging.logs.CompoundTree;

public class TreeTest {

	@Test
	public void buildTree() throws Exception {
		CompoundTree pkgs = new CompoundTree("Packages");
		pkgs.append(new CompoundTree("PKG", "net.runeduniverse.libs.logging.fake")
				.append(new CompoundTree("clAss", "Person").append("missing Class!")
						.append("FIeLd", "id")
						.append("fieD", "firstName")
						.append("field", "lastName"))
				.append(new CompoundTree("class", "House").append("yet another missing Class!")
						.append("field", "address")
						.append("field", "size")))
				.append(new CompoundTree("net.runeduniverse.libs.logging.fake.model"));

		CompoundTree rootTree = new CompoundTree("ROOT");
		rootTree.append(pkgs);
		System.out.println(rootTree);
	}

}
