/*
 * Copyright © 2022 Pl4yingNight (pl4yingnight@gmail.com)
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

import java.lang.reflect.Method;

import net.runeduniverse.lib.utils.scanner.pattern.MethodPattern;
import net.runeduniverse.lib.utils.scanner.pattern.TypePattern;

public interface IMethodScanner<M extends MethodPattern> {

	void scan(Method method, Class<?> type, TypePattern<?, M> pattern) throws Exception;

}
