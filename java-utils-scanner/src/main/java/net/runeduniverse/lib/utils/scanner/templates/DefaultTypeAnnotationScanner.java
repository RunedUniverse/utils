/*
 * Copyright © 2025 VenaNocta (venanocta@gmail.com)
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

import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import net.runeduniverse.lib.utils.scanner.pattern.api.FieldPattern;
import net.runeduniverse.lib.utils.scanner.pattern.api.MethodPattern;
import net.runeduniverse.lib.utils.scanner.pattern.api.TypePattern;

public class DefaultTypeAnnotationScanner<F extends FieldPattern, M extends MethodPattern, T extends TypePattern<F, M>>
		extends DefaultTypeScanner<F, M, T> {

	private final Class<? extends Annotation> anno;

	public DefaultTypeAnnotationScanner(Class<? extends Annotation> anno, PatternCreator<F, M, T> creator,
			ResultConsumer<F, M, T> consumer) {
		super(creator, consumer);
		this.anno = anno;
	}

	@Override
	public void scan(Class<?> type, ClassLoader loader, String pkg) throws Exception {
		if (type.isAnnotationPresent(this.anno))
			super.scan(type, loader, pkg);
	}

	public static DefaultTypeAnnotationScanner<FieldPattern, MethodPattern, TypePattern<FieldPattern, MethodPattern>> DEFAULT(
			Class<? extends Annotation> anno,
			ResultConsumer<FieldPattern, MethodPattern, TypePattern<FieldPattern, MethodPattern>> consumer) {
		return new DefaultTypeAnnotationScanner<>(anno, DefaultTypeScanner::createPattern, consumer);
	}

	public static DefaultTypeAnnotationScanner<FieldPattern, MethodPattern, TypePattern<FieldPattern, MethodPattern>> DEFAULT(
			Class<? extends Annotation> anno,
			ResultConsumer<FieldPattern, MethodPattern, TypePattern<FieldPattern, MethodPattern>> consumer,
			Consumer<DefaultTypeAnnotationScanner<FieldPattern, MethodPattern, TypePattern<FieldPattern, MethodPattern>>> handler) {
		final DefaultTypeAnnotationScanner<FieldPattern, MethodPattern, TypePattern<FieldPattern, MethodPattern>> scanner = DEFAULT(
				anno, consumer);
		if (handler != null) {
			handler.accept(scanner);
		}
		return scanner;
	}

}
