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
import java.util.function.Consumer;

import net.runeduniverse.lib.utils.scanner.api.FieldScanner;
import net.runeduniverse.lib.utils.scanner.pattern.DefaultFieldPattern;
import net.runeduniverse.lib.utils.scanner.pattern.api.FieldPattern;
import net.runeduniverse.lib.utils.scanner.pattern.api.TypePattern;

public class DefaultFieldScanner<F extends FieldPattern> implements FieldScanner<F> {

	protected final PatternCreator<F> creator;
	protected final ScanOrder order;

	public DefaultFieldScanner(PatternCreator<F> creator) {
		this.creator = creator;
		this.order = ScanOrder.ALL;
	}

	public DefaultFieldScanner(PatternCreator<F> creator, ScanOrder order) {
		this.creator = creator;
		this.order = order;
	}

	protected static FieldPattern createPattern(Field field) {
		return new DefaultFieldPattern(field);
	}

	@Override
	public void scan(Field field, Class<?> type, TypePattern<F, ?> pattern) throws Exception {
		withPattern(field, p -> {
			pattern.getFields(null)
					.add(p);
		});
	}

	protected void withPattern(final Field field, Consumer<F> consumer) throws Exception {
		final F pattern = this.creator.createPattern(field);
		if (pattern == null)
			return;
		consumer.accept(pattern);
	}

	public static DefaultFieldScanner<FieldPattern> DEFAULT() {
		return new DefaultFieldScanner<>(DefaultFieldScanner::createPattern);
	}

	public static DefaultFieldScanner<FieldPattern> DEFAULT(ScanOrder order) {
		return new DefaultFieldScanner<>(DefaultFieldScanner::createPattern, order);
	}

}
