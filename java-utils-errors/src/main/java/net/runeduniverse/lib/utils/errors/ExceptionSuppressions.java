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
package net.runeduniverse.lib.utils.errors;

import java.util.Collection;

public class ExceptionSuppressions extends ATrunkableException {
	private static final long serialVersionUID = 215607527815606000L;

	public ExceptionSuppressions(String message) {
		super(message);
	}

	public ExceptionSuppressions(String message, boolean trunk) {
		super(message, trunk);
	}

	public ExceptionSuppressions addSuppressed(Collection<Exception> errors) {
		for (Exception e : errors)
			this.addSuppressed(e);
		return this;
	}

	public boolean hasSuppressions() {
		return this.getSuppressed().length > 0;
	}
}
