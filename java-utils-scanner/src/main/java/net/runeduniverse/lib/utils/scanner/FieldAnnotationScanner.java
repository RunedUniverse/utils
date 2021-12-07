/*
 * Copyright © 2021 Pl4yingNight (pl4yingnight@gmail.com)
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
package net.runeduniverse.lib.utils.scanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class FieldAnnotationScanner<F extends FieldPattern> extends FieldScanner<F> {

	protected final Class<? extends Annotation> anno;

	public FieldAnnotationScanner(PatternCreator<F> creator, Class<? extends Annotation> anno) {
		super(creator);
		this.anno = anno;
	}

	public FieldAnnotationScanner(PatternCreator<F> creator, Class<? extends Annotation> anno, ScanOrder order) {
		super(creator, order);
		this.anno = anno;
	}

	@Override
	public void scan(Field field, Class<?> type, TypePattern<F, ?> pattern) throws Exception {
		switch (this.order) {
		case FIRST:
			if (pattern.getFields()
					.containsKey(this.anno))
				return;
		case LAST:
			pattern.getFields()
					.remove(this.anno);
		case ALL:
			if (field.isAnnotationPresent(this.anno))
				pattern.getFields()
						.put(this.anno, this.creator.createPattern(field));
		}
	}

	public static FieldAnnotationScanner<FieldPattern> DEFAULT(Class<? extends Annotation> anno) {
		return new FieldAnnotationScanner<FieldPattern>(FieldScanner::createPattern, anno);
	}

	public static FieldAnnotationScanner<FieldPattern> DEFAULT(Class<? extends Annotation> anno, ScanOrder order) {
		return new FieldAnnotationScanner<FieldPattern>(FieldScanner::createPattern, anno, order);
	}

}