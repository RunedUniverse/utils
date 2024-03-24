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
package net.runeduniverse.lib.utils.scanner.templates;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import lombok.RequiredArgsConstructor;
import net.runeduniverse.lib.utils.scanner.api.FieldScanner;
import net.runeduniverse.lib.utils.scanner.api.MethodScanner;
import net.runeduniverse.lib.utils.scanner.api.TypeScanner;
import net.runeduniverse.lib.utils.scanner.pattern.DefaultTypePattern;
import net.runeduniverse.lib.utils.scanner.pattern.api.FieldPattern;
import net.runeduniverse.lib.utils.scanner.pattern.api.MethodPattern;
import net.runeduniverse.lib.utils.scanner.pattern.api.TypePattern;

@RequiredArgsConstructor
public class DefaultTypeScanner<F extends FieldPattern, M extends MethodPattern, T extends TypePattern<F, M>>
		implements TypeScanner {

	protected final PatternCreator<F, M, T> creator;
	protected final ResultConsumer<F, M, T> consumer;

	protected SCN<F, M, T> scanFieldsFnc = DefaultTypeScanner::scanFields;
	protected SCN<F, M, T> scanMethodsFnc = DefaultTypeScanner::scanMethods;
	protected Set<FieldScanner<F>> fieldScanner = new HashSet<>();
	protected Set<MethodScanner<M>> methodScanner = new HashSet<>();

	protected static TypePattern<FieldPattern, MethodPattern> createPattern(Class<?> type, ClassLoader loader,
			String pkg) {
		return new DefaultTypePattern<>(pkg, loader, type);
	}

	public DefaultTypeScanner<F, M, T> addFieldScanner(FieldScanner<F> fieldScanner) {
		this.fieldScanner.add(fieldScanner);
		return this;
	}

	public DefaultTypeScanner<F, M, T> addMethodScanner(MethodScanner<M> methodScanner) {
		this.methodScanner.add(methodScanner);
		return this;
	}

	@Override
	public void scan(Class<?> type, ClassLoader loader, String pkg) throws Exception {
		final T pattern = this.creator.createPattern(type, loader, pkg);
		DefaultTypeScanner.cascade(this, this.scanFieldsFnc, pattern, pattern.getType());
		DefaultTypeScanner.cascade(this, this.scanMethodsFnc, pattern, pattern.getType());
		this.consumer.accept(pattern);
	}

	@FunctionalInterface
	public static interface ResultConsumer<F extends FieldPattern, M extends MethodPattern, T extends TypePattern<F, M>> {
		void accept(T pattern) throws Exception;
	}

	@FunctionalInterface
	protected static interface SCN<F extends FieldPattern, M extends MethodPattern, T extends TypePattern<F, M>> {
		void accept(DefaultTypeScanner<F, M, T> scanner, T pattern, Class<?> type) throws Exception;
	}

	protected static <F extends FieldPattern, M extends MethodPattern, T extends TypePattern<F, M>> void cascade(
			DefaultTypeScanner<F, M, T> scanner, SCN<F, M, T> scn, T pattern, Class<?> type) throws Exception {
		scn.accept(scanner, pattern, type);
		if (type.getSuperclass()
				.equals(Object.class))
			return;
		cascade(scanner, scn, pattern, type.getSuperclass());
	}

	protected static <F extends FieldPattern, M extends MethodPattern, T extends TypePattern<F, M>> void scanFields(
			DefaultTypeScanner<F, M, T> scanner, T pattern, Class<?> type) throws Exception {
		for (Field f : type.getDeclaredFields())
			for (FieldScanner<F> fscan : scanner.fieldScanner)
				fscan.scan(f, type, pattern);
	}

	protected static <F extends FieldPattern, M extends MethodPattern, T extends TypePattern<F, M>> void scanMethods(
			DefaultTypeScanner<F, M, T> scanner, T pattern, Class<?> type) throws Exception {
		for (Method m : type.getDeclaredMethods())
			for (MethodScanner<M> mscan : scanner.methodScanner)
				mscan.scan(m, type, pattern);
	}

	public static DefaultTypeScanner<FieldPattern, MethodPattern, TypePattern<FieldPattern, MethodPattern>> DEFAULT(
			ResultConsumer<FieldPattern, MethodPattern, TypePattern<FieldPattern, MethodPattern>> consumer) {
		return new DefaultTypeScanner<>(DefaultTypeScanner::createPattern, consumer);
	}

	public static DefaultTypeScanner<FieldPattern, MethodPattern, TypePattern<FieldPattern, MethodPattern>> DEFAULT(
			ResultConsumer<FieldPattern, MethodPattern, TypePattern<FieldPattern, MethodPattern>> consumer,
			Consumer<DefaultTypeScanner<FieldPattern, MethodPattern, TypePattern<FieldPattern, MethodPattern>>> handler) {
		final DefaultTypeScanner<FieldPattern, MethodPattern, TypePattern<FieldPattern, MethodPattern>> scanner = DEFAULT(
				consumer);
		if (handler != null) {
			handler.accept(scanner);
		}
		return scanner;
	}

}
