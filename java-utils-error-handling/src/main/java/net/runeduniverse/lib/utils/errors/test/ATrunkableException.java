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
package net.runeduniverse.lib.utils.errors.test;

import java.util.ArrayList;
import java.util.List;

public abstract class ATrunkableException extends Exception {
	private static final long serialVersionUID = -3060179675399411806L;

	public ATrunkableException(String message) {
		super(message);
	}

	public ATrunkableException(String message, Throwable cause) {
		super(message, cause);
	}

	public ATrunkableException(String message, boolean trunk) {
		super(ATrunkableException.addPrefix(message, trunk));
		if (trunk)
			this.trunk();
	}

	public ATrunkableException(String message, Throwable cause, boolean trunk) {
		super(ATrunkableException.addPrefix(message, trunk), cause);
		if (trunk)
			this.trunk();
	}

	private static String addPrefix(String message, boolean trunk) {
		return trunk ? "[TRUNKED] " + message : message;
	}

	protected void trunk() {
		List<StackTraceElement> trace = new ArrayList<>();
		for (StackTraceElement element : this.getStackTrace())
			trace.add(element);
		this.trunkStackTrace(trace);
		this.setStackTrace(trace.toArray(new StackTraceElement[trace.size()]));
		this.printStackTrace();
	}

	protected void trunkStackTrace(List<StackTraceElement> trace) {
		trace.removeIf(ATrunkableException::removeJUnitStackTrace);
	}

	protected static boolean removeJUnitStackTrace(StackTraceElement element) {
		return ATrunkableException.pathStartsWith(element, "org.junit.");
	}

	protected static boolean pathStartsWith(StackTraceElement element, String prefix) {
		return element.getClassName()
				.startsWith(prefix);
	}
}
