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

import net.runeduniverse.lib.utils.scanner.ScanOrder;
import net.runeduniverse.lib.utils.scanner.pattern.MethodPattern;
import net.runeduniverse.lib.utils.scanner.pattern.TypePattern;

public class MethodAnnotationScanner<M extends MethodPattern> extends MethodScanner<M> {

	private final Class<? extends Annotation> anno;

	public MethodAnnotationScanner(PatternCreator<M> creator, Class<? extends Annotation> anno) {
		super(creator);
		this.anno = anno;
	}

	public MethodAnnotationScanner(PatternCreator<M> creator, Class<? extends Annotation> anno, ScanOrder order) {
		super(creator, order);
		this.anno = anno;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void scan(Method method, Class<?> type, TypePattern<?, M> pattern) throws Exception {
		switch (this.order) {
		case FIRST:
			if (pattern.hasMethods(this.anno))
				return;
		case LAST:
			pattern.getFields()
					.remove(this.anno);
		case ALL:
			if (method.isAnnotationPresent(this.anno))
				super.scan(method, type, pattern);
		}
	}

	public static MethodAnnotationScanner<MethodPattern> DEFAULT(Class<? extends Annotation> anno) {
		return new MethodAnnotationScanner<>(MethodScanner::createPattern, anno);
	}

	public static MethodAnnotationScanner<MethodPattern> DEFAULT(Class<? extends Annotation> anno, ScanOrder order) {
		return new MethodAnnotationScanner<>(MethodScanner::createPattern, anno, order);
	}

}
