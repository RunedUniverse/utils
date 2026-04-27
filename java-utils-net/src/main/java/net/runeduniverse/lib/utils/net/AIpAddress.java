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
package net.runeduniverse.lib.utils.net;

import net.runeduniverse.lib.utils.net.api.IpAddress;

public abstract class AIpAddress {

	public abstract boolean equals(final IpAddress<?> ip);

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof IpAddress<?>)
			return equals((IpAddress<?>) obj);
		return super.equals(obj);
	}

	public int compareTo(final IpAddress<?> a, final IpAddress<?> b) {
		// null check => null < value
		if (a == null) {
			if (b == null)
				return 0;
			return -1;
		}
		if (b == null)
			return 1;
		// handle differences in family
		final short familyA = a.getIpFamily();
		final short familyB = b.getIpFamily();

		if (familyA < familyB) {
			return -1;
		}
		if (familyB < familyA) {
			return 1;
		}
		// if they are of the same family => compare the binary values
		return IpBinaryUtils.compareTo(a.getBinaryAddress(), b.getBinaryAddress());
	}
}
