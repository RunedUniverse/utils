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
package net.runeduniverse.lib.utils.scanner.api;

import net.runeduniverse.lib.utils.scanner.pattern.api.FieldPattern;
import net.runeduniverse.lib.utils.scanner.pattern.api.MethodPattern;
import net.runeduniverse.lib.utils.scanner.pattern.api.TypePattern;

public interface TypeScanner {

	void scan(Class<?> type, ClassLoader loader, String pkg) throws Exception;

	@FunctionalInterface
	public static interface PatternCreator<F extends FieldPattern, M extends MethodPattern, T extends TypePattern<F, M>> {
		T createPattern(Class<?> type, ClassLoader loader, String pkg) throws Exception;
	}

}
