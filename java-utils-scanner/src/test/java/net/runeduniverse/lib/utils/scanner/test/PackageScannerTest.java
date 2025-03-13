/*
 * Copyright © 2024 VenaNocta (venanocta@gmail.com)
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

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import net.runeduniverse.lib.utils.logging.log.DefaultCompoundTree;
import net.runeduniverse.lib.utils.logging.log.api.CompoundTree;
import net.runeduniverse.lib.utils.scanner.*;
import net.runeduniverse.lib.utils.scanner.api.TypeScanner;
import net.runeduniverse.lib.utils.scanner.pattern.api.FieldPattern;
import net.runeduniverse.lib.utils.scanner.pattern.api.MethodPattern;
import net.runeduniverse.lib.utils.scanner.pattern.api.TypePattern;
import net.runeduniverse.lib.utils.scanner.templates.DefaultFieldAnnotationScanner;
import net.runeduniverse.lib.utils.scanner.templates.DefaultFieldScanner;
import net.runeduniverse.lib.utils.scanner.templates.DefaultMethodAnnotationScanner;
import net.runeduniverse.lib.utils.scanner.templates.DefaultMethodScanner;
import net.runeduniverse.lib.utils.scanner.templates.DefaultTypeAnnotationScanner;
import net.runeduniverse.lib.utils.scanner.templates.DefaultTypeScanner;
import net.runeduniverse.lib.utils.scanner.templates.ScanOrder;
import net.runeduniverse.lib.utils.scanner.test.annotations.FirstField;
import net.runeduniverse.lib.utils.scanner.test.annotations.FirstMethod;
import net.runeduniverse.lib.utils.scanner.test.annotations.LastField;
import net.runeduniverse.lib.utils.scanner.test.annotations.LastMethod;
import net.runeduniverse.lib.utils.scanner.test.annotations.LivingEntity;
import net.runeduniverse.lib.utils.scanner.test.model.Emmy;
import net.runeduniverse.lib.utils.scanner.test.model.Frank;

public class PackageScannerTest {

	public static final String MODEL_PKG = "net.runeduniverse.lib.utils.scanner.test.model";

	private static PackageScanner SEEDED_SCANNER() {
		return new PackageScanner().enableDebugMode(true)
				.includeClassLoader(PackageScannerTest.class.getClassLoader())
				.includePackages(MODEL_PKG)
				.includeSubPkgs();
	}

	@Test
	@Tag("system")
	public void scanAllCalasses() throws Exception {
		Set<TypePattern<FieldPattern, MethodPattern>> foundTypes = new HashSet<>();
		PackageScanner scanner = SEEDED_SCANNER()
				.includeScanner(new TypeScanner[] { DefaultTypeScanner.DEFAULT(t -> foundTypes.add(t)) });
		scanner.scan()
				.throwSurpressions();

		CompoundTree tree = new DefaultCompoundTree("Found Classes");
		for (TypePattern<FieldPattern, MethodPattern> typePattern : foundTypes)
			tree.append(typePattern.getType()
					.getCanonicalName());
		System.out.println(tree.toString());
		assertEquals(3, foundTypes.size(), "Too many/few Classes found!");
	}

	// ! ATTENTION !
	// Disabled scan order tests as they can vary based on compiler optimization!
	@Test
	@Tag("system")
	public void scanLivingEntities() throws Exception {
		Set<TypePattern<FieldPattern, MethodPattern>> foundTypes = new HashSet<>();
		PackageScanner scanner = SEEDED_SCANNER().includeScanner(new TypeScanner[] {
				DefaultTypeAnnotationScanner.DEFAULT(LivingEntity.class, t -> foundTypes.add(t), s -> {
					// field scanners
					s.addFieldScanner(DefaultFieldScanner.DEFAULT(ScanOrder.ALL));
					s.addFieldScanner(DefaultFieldAnnotationScanner.DEFAULT(FirstField.class, ScanOrder.FIRST));
					s.addFieldScanner(DefaultFieldAnnotationScanner.DEFAULT(LastField.class, ScanOrder.LAST));
					// method scanners
					s.addMethodScanner(DefaultMethodScanner.DEFAULT(ScanOrder.ALL));
					s.addMethodScanner(DefaultMethodAnnotationScanner.DEFAULT(FirstMethod.class, ScanOrder.FIRST));
					s.addMethodScanner(DefaultMethodAnnotationScanner.DEFAULT(LastMethod.class, ScanOrder.LAST));
				}) });
		scanner.scan()
				.throwSurpressions();

		boolean classEmmyFound = false;
		boolean classFrankFound = false;

		CompoundTree tree = new DefaultCompoundTree("Found Classes");
		for (TypePattern<FieldPattern, MethodPattern> typePattern : foundTypes) {
			tree.append(typePattern.getType()
					.getCanonicalName());

			if (Emmy.class.isAssignableFrom(typePattern.getType())) {
				classEmmyFound = true;
				System.out.println("Checking Scanned Fields");
				// validate the method scanners
				Set<FieldPattern> col = typePattern.getFields(FirstField.class);
				assertEquals(1, col.size(), "Too many/few @FirstField found!");
				// ! assertEquals("plants", typePattern.getField(FirstField.class)
				// ! .getField()
				// ! .getName(), "Wrong FirstField!");
				//
				col = typePattern.getFields(LastField.class);
				assertEquals(1, col.size(), "Too many/few @LastField found!");
				// ! assertEquals("age", typePattern.getField(LastField.class)
				// ! .getField()
				// ! .getName(), "Wrong LastField!");
				assertEquals(3, typePattern.getFields()
						.size(), "Too many/few Fields found!");
			}

			if (Frank.class.isAssignableFrom(typePattern.getType())) {
				classFrankFound = true;
				System.out.println("Checking Scanned Methods");
				// validate the method scanners
				Set<MethodPattern> col = typePattern.getMethods(FirstMethod.class);
				assertEquals(1, col.size(), "Too many/few @FirstMethod found!");
				// ! assertEquals("wave", typePattern.getMethod(FirstMethod.class)
				// ! .getMethod()
				// ! .getName(), "Wrong FirstMethod!");
				//
				col = typePattern.getMethods(LastMethod.class);
				assertEquals(1, col.size(), "Too many/few @LastMethod found!");
				// ! assertEquals("hi", typePattern.getMethod(LastMethod.class)
				// ! .getMethod()
				// ! .getName(), "Wrong LastMethod!");
				assertEquals(3, typePattern.getMethods()
						.size(), "Too many/few Methods found!");
			}

		}

		assertEquals(true, classEmmyFound, "Field Tests not processed!");
		assertEquals(true, classFrankFound, "Method Tests not processed!");
		System.out.println(tree.toString());
		assertEquals(3, foundTypes.size(), "Too many/few Classes found!");
	}

}
