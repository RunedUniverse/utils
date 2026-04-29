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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import net.runeduniverse.lib.utils.net.api.IpAddress;
import net.runeduniverse.lib.utils.net.api.Ip4Address;

public class DefaultIp4Address extends AIpAddress<Ip4Address> implements Ip4Address {

	private static final long serialVersionUID = 1L;

	public DefaultIp4Address() {
		super(new byte[4]);
	}

	public DefaultIp4Address(final byte byte0, final byte byte1, final byte byte2, final byte byte3) {
		// byte0.byte1.byte2.byte3
		// data[0].data[1].data[2].data[3]
		super(new byte[] { byte0, byte1, byte2, byte3 });
	}

	public DefaultIp4Address(final byte[] data) {
		super(data);
	}

	@Override
	protected boolean isApplicable(final IpAddress<?> instance) {
		return instance instanceof Ip4Address;
	}

	@Override
	public Ip4Address copy() {
		return new DefaultIp4Address(this.data);
	}

	@Override
	public Ip4Address increment() {
		final byte[] data = IpBinaryUtils.increment(this.data);
		if (data.length == 0)
			return null;
		return new DefaultIp4Address(data);
	}

	@Override
	public String toCidrNotation() {
		return IpBinaryUtils.toCidrNotation(this.data, ".", 1, 10);
	}

	@Override
	public Inet4Address toInetAddress(final String host) {
		try {
			final InetAddress address = InetAddress.getByAddress(host, this.data);
			if (address instanceof Inet4Address)
				return (Inet4Address) address;
		} catch (UnknownHostException ignored) {
			ignored.printStackTrace();
		}
		return null;
	}
}
