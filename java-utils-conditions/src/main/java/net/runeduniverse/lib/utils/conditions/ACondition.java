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
package net.runeduniverse.lib.utils.conditions;

import net.runeduniverse.lib.utils.conditions.api.Condition;

public abstract class ACondition<T> implements Condition<T> {

	private Integer hashCode = null;

	@Override
	public boolean evaluate(final T entity) {
		return and(this::conditionIsValid, check()).eval(entity);
	}

	protected boolean conditionIsValid(final T data) {
		return isValid();
	}

	protected abstract DataCheck<T> check();

	@Override
	public int hashCode() {
		if (this.hashCode == null) {
			final String hashable = getType();
			this.hashCode = hashable == null ? 0 : hashable.hashCode();
		}
		return this.hashCode;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		return obj instanceof Condition<?> && hashCode() == obj.hashCode()
				&& getPriority() == ((Condition<?>) obj).getPriority();
	}

	protected static <T> DataCheck<T> isNull() {
		return d -> d == null;
	}

	protected static <T> DataCheck<T> nonNull() {
		return d -> d != null;
	}

	@SafeVarargs
	protected static <T> DataCheck<T> and(final DataCheck<T>... checks) {
		return new DataCheck<T>() {
			@Override
			public boolean eval(T data) {
				for (DataCheck<T> check : checks) {
					if (!check.eval(data))
						return false;
				}
				return true;
			}
		};
	}

	@SafeVarargs
	protected static <T> DataCheck<T> or(final DataCheck<T>... checks) {
		return new DataCheck<T>() {
			@Override
			public boolean eval(T data) {
				for (DataCheck<T> check : checks) {
					if (check.eval(data))
						return true;
				}
				return false;
			}
		};
	}

	protected static <T> DataCheck<T> not(final DataCheck<T> check) {
		return new DataCheck<T>() {
			@Override
			public boolean eval(T data) {
				return !check.eval(data);
			}
		};
	}

	@FunctionalInterface
	public interface DataCheck<T> {

		public boolean eval(final T data);

	}
}
