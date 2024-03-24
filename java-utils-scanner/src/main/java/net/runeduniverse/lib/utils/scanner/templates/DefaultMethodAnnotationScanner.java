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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import net.runeduniverse.lib.utils.scanner.pattern.api.MethodPattern;
import net.runeduniverse.lib.utils.scanner.pattern.api.TypePattern;

public class DefaultMethodAnnotationScanner<M extends MethodPattern> extends DefaultMethodScanner<M> {

	private final Class<? extends Annotation> anno;

	public DefaultMethodAnnotationScanner(PatternCreator<M> creator, Class<? extends Annotation> anno) {
		super(creator);
		this.anno = anno;
	}

	public DefaultMethodAnnotationScanner(PatternCreator<M> creator, Class<? extends Annotation> anno,
			ScanOrder order) {
		super(creator, order);
		this.anno = anno;
	}

	@Override
	public void scan(Method method, Class<?> type, TypePattern<?, M> pattern) throws Exception {
		final Set<M> methods = pattern.getMethods(anno);
		switch (this.order) {
		case FIRST:
			if (!methods.isEmpty())
				return;
		case LAST:
		case ALL:
			if (method.isAnnotationPresent(this.anno)) {
				withPattern(method, f -> {
					if (DefaultMethodAnnotationScanner.this.order == ScanOrder.LAST)
						methods.clear();
					methods.add(f);
				});
			}
		}
	}

	public static DefaultMethodAnnotationScanner<MethodPattern> DEFAULT(Class<? extends Annotation> anno) {
		return new DefaultMethodAnnotationScanner<>(DefaultMethodScanner::createPattern, anno);
	}

	public static DefaultMethodAnnotationScanner<MethodPattern> DEFAULT(Class<? extends Annotation> anno,
			ScanOrder order) {
		return new DefaultMethodAnnotationScanner<>(DefaultMethodScanner::createPattern, anno, order);
	}

}
