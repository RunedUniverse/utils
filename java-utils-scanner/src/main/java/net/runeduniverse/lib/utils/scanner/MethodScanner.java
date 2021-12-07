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

import java.lang.reflect.Method;

public class MethodScanner<M extends MethodPattern> implements IMethodScanner<M> {

	protected final PatternCreator<M> creator;
	protected final ScanOrder order;

	public MethodScanner(PatternCreator<M> creator) {
		this.creator = creator;
		this.order = ScanOrder.ALL;
	}

	public MethodScanner(PatternCreator<M> creator, ScanOrder order) {
		this.creator = creator;
		this.order = order;
	}

	protected static MethodPattern createPattern(Method method) {
		return new MethodPattern(method);
	}

	@Override
	public void scan(Method method, Class<?> type, TypePattern<?, M> pattern) throws Exception {
		pattern.getMethods()
				.put(null, this.creator.createPattern(method));
	}

	@FunctionalInterface
	public static interface PatternCreator<M extends MethodPattern> {
		M createPattern(Method method) throws Exception;
	}

	public static MethodScanner<MethodPattern> DEFAULT() {
		return new MethodScanner<MethodPattern>(MethodScanner::createPattern);
	}

	public static MethodScanner<MethodPattern> DEFAULT(ScanOrder order) {
		return new MethodScanner<MethodPattern>(MethodScanner::createPattern, order);
	}

}