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

import java.lang.reflect.Method;
import java.util.function.Consumer;

import net.runeduniverse.lib.utils.scanner.api.MethodScanner;
import net.runeduniverse.lib.utils.scanner.pattern.DefaultMethodPattern;
import net.runeduniverse.lib.utils.scanner.pattern.api.MethodPattern;
import net.runeduniverse.lib.utils.scanner.pattern.api.TypePattern;

public class DefaultMethodScanner<M extends MethodPattern> implements MethodScanner<M> {

	protected final PatternCreator<M> creator;
	protected final ScanOrder order;

	public DefaultMethodScanner(PatternCreator<M> creator) {
		this.creator = creator;
		this.order = ScanOrder.ALL;
	}

	public DefaultMethodScanner(PatternCreator<M> creator, ScanOrder order) {
		this.creator = creator;
		this.order = order;
	}

	protected static MethodPattern createPattern(Method method) {
		return new DefaultMethodPattern(method);
	}

	@Override
	public void scan(Method method, Class<?> type, TypePattern<?, M> pattern) throws Exception {
		withPattern(method, p -> {
			pattern.getMethods(null)
					.add(p);
		});
	}

	protected void withPattern(final Method method, Consumer<M> consumer) throws Exception {
		final M pattern = this.creator.createPattern(method);
		if (pattern == null)
			return;
		consumer.accept(pattern);
	}

	public static DefaultMethodScanner<MethodPattern> DEFAULT() {
		return new DefaultMethodScanner<>(DefaultMethodScanner::createPattern);
	}

	public static DefaultMethodScanner<MethodPattern> DEFAULT(ScanOrder order) {
		return new DefaultMethodScanner<>(DefaultMethodScanner::createPattern, order);
	}

}
