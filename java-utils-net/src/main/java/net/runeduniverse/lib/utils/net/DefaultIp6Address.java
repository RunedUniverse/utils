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

public class DefaultIp6Address extends AIpAddress<Ip6Address> implements Ip6Address {

	private static final long serialVersionUID = 1L;

	public DefaultIp6Address() {
		super(new byte[16]);
	}

	public DefaultIp6Address(final byte[] data) {
		super(data);
	}

	@Override
	protected boolean isApplicable(final IpAddress<?> instance) {
		return instance instanceof Ip6Address;
	}

	@Override
	public Ip6Address copy() {
		return new DefaultIp6Address(this.data);
	}

	@Override
	public Ip6Address increment() {
		final byte[] data = IpBinaryUtils.increment(this.data);
		if (data.length == 0)
			return null;
		return new DefaultIp6Address(data);
	}

	@Override
	public String toCidrNotation() {
		return IpBinaryUtils.toCidrNotation(this.data, ":", 2, 16);
	}

	@Override
	public Inet6Address toInetAddress(final String host) {
		try {
			return Inet6Address.getByAddress(host, this.data, null);
		} catch (UnknownHostException ignored) {
			ignored.printStackTrace();
		}
		return null;
	}
}
