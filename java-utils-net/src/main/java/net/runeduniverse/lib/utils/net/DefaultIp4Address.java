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

import java.lang.reflect.InvocationTargetException;
import java.net.Inet4Address;

import net.runeduniverse.lib.utils.net.api.IpAddress;
import net.runeduniverse.lib.utils.net.api.Ip4Address;

public class DefaultIp4Address extends AIpAddress implements Ip4Address {

	private final byte[] data;

	public DefaultIp4Address() {
		this(new byte[4]);
	}

	public DefaultIp4Address(final byte byte0, final byte byte1, final byte byte2, final byte byte3) {
		// byte0.byte1.byte2.byte3
		// data[0].data[1].data[2].data[3]
		this.data = new byte[] { byte0, byte1, byte2, byte3 };
	}

	public DefaultIp4Address(final byte[] data) {
		this.data = data;
	}

	@Override
	public boolean equals(final IpAddress<?> ip) {
		if (ip instanceof Ip4Address) {
			return compareTo((Ip4Address) ip) == 0;
		}
		return false;
	}

	@Override
	public int compareTo(final IpAddress<?> address) {
		return compareTo(this, address);
	}

	@Override
	public Ip4Address copy() {
		return new DefaultIp4Address(this.data);
	}

	@Override
	public String toCidrNotation() {
		return IpBinaryUtils.toCidrNotation(this.data, ".", 1, 10);
	}

	@Override
	public int hashCode() {
		return toCidrNotation().hashCode();
	}

	@Override
	public byte[] getBinaryAddress() {
		return this.data;
	}

	@Override
	public Inet4Address toInetAddress() {
		try {
			return Inet4Address.class.getDeclaredConstructor(String.class, byte[].class)
					.newInstance(null, this.data);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException ignored) {
			return null;
		}
	}
}
