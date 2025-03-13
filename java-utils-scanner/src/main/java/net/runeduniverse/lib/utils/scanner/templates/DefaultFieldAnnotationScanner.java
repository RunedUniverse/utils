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
import java.lang.reflect.Field;
import java.util.Set;

import net.runeduniverse.lib.utils.scanner.pattern.api.FieldPattern;
import net.runeduniverse.lib.utils.scanner.pattern.api.TypePattern;

public class DefaultFieldAnnotationScanner<F extends FieldPattern> extends DefaultFieldScanner<F> {

	protected final Class<? extends Annotation> anno;

	public DefaultFieldAnnotationScanner(PatternCreator<F> creator, Class<? extends Annotation> anno) {
		super(creator);
		this.anno = anno;
	}

	public DefaultFieldAnnotationScanner(PatternCreator<F> creator, Class<? extends Annotation> anno, ScanOrder order) {
		super(creator, order);
		this.anno = anno;
	}

	@Override
	public void scan(Field field, Class<?> type, TypePattern<F, ?> pattern) throws Exception {
		final Set<F> fields = pattern.getFields(anno);
		switch (this.order) {
		case FIRST:
			if (!fields.isEmpty())
				return;
		case LAST:
		case ALL:
			if (field.isAnnotationPresent(this.anno)) {
				withPattern(field, f -> {
					if (DefaultFieldAnnotationScanner.this.order == ScanOrder.LAST)
						fields.clear();
					fields.add(f);
				});
			}
		}
	}

	public static DefaultFieldAnnotationScanner<FieldPattern> DEFAULT(Class<? extends Annotation> anno) {
		return new DefaultFieldAnnotationScanner<>(DefaultFieldScanner::createPattern, anno);
	}

	public static DefaultFieldAnnotationScanner<FieldPattern> DEFAULT(Class<? extends Annotation> anno,
			ScanOrder order) {
		return new DefaultFieldAnnotationScanner<>(DefaultFieldScanner::createPattern, anno, order);
	}

}
