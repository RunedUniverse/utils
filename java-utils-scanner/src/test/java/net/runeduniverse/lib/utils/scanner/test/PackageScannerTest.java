/*
 * Copyright © 2022 Pl4yingNight (pl4yingnight@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.runeduniverse.lib.utils.scanner.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import net.runeduniverse.lib.utils.logging.logs.CompoundTree;
import net.runeduniverse.lib.utils.scanner.*;
import net.runeduniverse.lib.utils.scanner.api.ITypeScanner;
import net.runeduniverse.lib.utils.scanner.pattern.FieldPattern;
import net.runeduniverse.lib.utils.scanner.pattern.MethodPattern;
import net.runeduniverse.lib.utils.scanner.pattern.TypePattern;
import net.runeduniverse.lib.utils.scanner.templates.TypeAnnotationScanner;
import net.runeduniverse.lib.utils.scanner.templates.TypeScanner;
import net.runeduniverse.lib.utils.scanner.test.annotations.LivingEntity;

public class PackageScannerTest {

	public static final String MODEL_PKG = "net.runeduniverse.lib.utils.scanner.test.model";

	private static PackageScanner SEEDED_SCANNER() {
		return new PackageScanner().enableDebugMode(true)README.md
				.includeClassLoader(PackageScannerTest.class.getClassLoader())
				.includePackages(MODEL_PKG)
				.includeSubPkgs();
	}

	@Test
	public void scanAllCalasses() throws Exception {
		Set<TypePattern<FieldPattern, MethodPattern>> foundTypes = new HashSet<>();
		PackageScanner scanner = SEEDED_SCANNER()
				.includeScanner(new ITypeScanner[] { TypeScanner.DEFAULT(t -> foundTypes.add(t)) });
		scanner.scan()
				.throwSurpressions();

		CompoundTree tree = new CompoundTree("Found Classes");
		for (TypePattern<FieldPattern, MethodPattern> typePattern : foundTypes)
			tree.append(typePattern.getType()
					.getCanonicalName());
		System.out.println(tree.toString());
		assertEquals(foundTypes.size(), 3, "Too many/few Classes found!");
	}

	@Test
	public void scanLivingEntities() throws Exception {
		Set<TypePattern<FieldPattern, MethodPattern>> foundTypes = new HashSet<>();
		PackageScanner scanner = SEEDED_SCANNER().includeScanner(
				new ITypeScanner[] { TypeAnnotationScanner.DEFAULT(LivingEntity.class, t -> foundTypes.add(t)) });
		scanner.scan()
				.throwSurpressions();

		CompoundTree tree = new CompoundTree("Found Classes");
		for (TypePattern<FieldPattern, MethodPattern> typePattern : foundTypes)
			tree.append(typePattern.getType()
					.getCanonicalName());
		System.out.println(tree.toString());
		assertEquals(foundTypes.size(), 3, "Too many/few Classes found!");
	}

}
