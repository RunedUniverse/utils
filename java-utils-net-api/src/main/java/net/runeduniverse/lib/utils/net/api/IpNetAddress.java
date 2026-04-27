/*
 * Copyright © 2026 VenaNocta (venanocta@gmail.com)
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
package net.runeduniverse.lib.utils.net.api;

import java.util.Comparator;
import java.util.function.Function;

public interface IpNetAddress<A extends IpAddress<A>, T extends IpNetAddress<A, T>> extends CidrObject {

	public boolean equals(IpNetAddress<?, ?> subnet);

	public boolean equals(IpNetAddress<?, ?> subnet, short mask);

	public A getBaseAddress();

	public A getLowestAddress();

	public A getHighestAddress();

	public short getMask();

	public T copy();

	public boolean isSupernetOf(IpNetAddress<?, ?> subnet);

	public boolean contains(IpAddress<?> address);

	public static <A extends IpAddress<A>, S extends IpNetAddress<A, S>> Comparator<S> compareByAddress(
			final Function<S, A> selector) {
		return new Comparator<S>() {
			@Override
			public int compare(final S n0, final S n1) {
				if (n0 == null) {
					if (n1 == null)
						return 0;
					return -1;
				}
				if (n1 == null)
					return 1;

				final A ip0 = selector.apply(n0);
				final A ip1 = selector.apply(n1);
				if (ip0 == null) {
					if (ip1 == null)
						return 0;
					return -1;
				}
				if (ip1 == null)
					return 1;

				return ip0.compareTo(ip1);
			}
		};
	}

	public static <A extends IpAddress<A>, S extends IpNetAddress<A, S>> Comparator<S> compareByBaseAddress() {
		return compareByAddress(S::getBaseAddress);
	}

	public static <A extends IpAddress<A>, S extends IpNetAddress<A, S>> Comparator<S> compareByLowestAddress() {
		return compareByAddress(S::getLowestAddress);
	}

	public static <A extends IpAddress<A>, S extends IpNetAddress<A, S>> Comparator<S> compareByHighestAddress() {
		return compareByAddress(S::getHighestAddress);
	}
}
