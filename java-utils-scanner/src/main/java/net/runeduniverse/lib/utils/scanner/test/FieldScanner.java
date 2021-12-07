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
package net.runeduniverse.lib.utils.scanner.test;

import java.lang.reflect.Field;

public class FieldScanner<F extends FieldPattern> implements IFieldScanner<F> {

	protected final PatternCreator<F> creator;
	protected final ScanOrder order;

	public FieldScanner(PatternCreator<F> creator) {
		this.creator = creator;
		this.order = ScanOrder.ALL;
	}

	public FieldScanner(PatternCreator<F> creator, ScanOrder order) {
		this.creator = creator;
		this.order = order;
	}

	protected static FieldPattern createPattern(Field field) {
		return new FieldPattern(field);
	}

	@Override
	public void scan(Field field, Class<?> type, TypePattern<F, ?> pattern) throws Exception {
		F p = this.creator.createPattern(field);
		if (p != null)
			pattern.getFields()
					.put(null, p);
	}

	@FunctionalInterface
	public static interface PatternCreator<F extends FieldPattern> {
		F createPattern(Field field) throws Exception;
	}

	public static FieldScanner<FieldPattern> DEFAULT() {
		return new FieldScanner<FieldPattern>(FieldScanner::createPattern);
	}

	public static FieldScanner<FieldPattern> DEFAULT(ScanOrder order) {
		return new FieldScanner<FieldPattern>(FieldScanner::createPattern, order);
	}
}
