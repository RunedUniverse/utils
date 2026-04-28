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

import java.net.Inet6Address;
import java.net.UnknownHostException;

import net.runeduniverse.lib.utils.net.api.IpAddress;
import net.runeduniverse.lib.utils.net.api.Ip6Address;

public class DefaultIp6Address extends AIpAddress implements Ip6Address {

	private final byte[] data;

	public DefaultIp6Address() {
		this(new byte[16]);
	}

	public DefaultIp6Address(final byte[] data) {
		this.data = data;
	}

	@Override
	public boolean equals(final IpAddress<?> address) {
		if (address instanceof Ip6Address)
			return compareTo((Ip6Address) address) == 0;
		return false;
	}

	@Override
	public int compareTo(final IpAddress<?> address) {
		return compareTo(this, address);
	}

	@Override
	public Ip6Address copy() {
		return new DefaultIp6Address(this.data);
	}

	@Override
	public byte[] getBinaryAddress() {
		return this.data;
	}

	@Override
	public String toCidrNotation() {
		return IpBinaryUtils.toCidrNotation(this.data, ":", 2, 16);
	}

	@Override
	public int hashCode() {
		return toCidrNotation().hashCode();
	}

	@Override
	public Inet6Address toInetAddress() {
		try {
			return Inet6Address.getByAddress(null, this.data, null);
		} catch (UnknownHostException ignored) {
			ignored.printStackTrace();
		}
		return null;
	}
}
