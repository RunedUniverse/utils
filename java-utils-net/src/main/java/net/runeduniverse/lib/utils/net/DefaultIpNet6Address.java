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

import java.util.Collection;

import net.runeduniverse.lib.utils.net.api.Ip6Address;
import net.runeduniverse.lib.utils.net.api.IpNet6Address;
import net.runeduniverse.lib.utils.net.api.IpNetAddress;

public class DefaultIpNet6Address extends AIpNetAddress<Ip6Address, IpNet6Address> implements IpNet6Address {

	public DefaultIpNet6Address(final Ip6Address address) {
		this(address, (short) 128);
	}

	public DefaultIpNet6Address(final Ip6Address address, final short mask) {
		super(address, mask);
	}

	@Override
	protected boolean isApplicable(final IpNetAddress<?, ?> instance) {
		return instance instanceof IpNet6Address;
	}

	@Override
	public IpNet6Address copy() {
		return new DefaultIpNet6Address(this.address, this.mask);
	}

	@Override
	public Ip6Address getLowestAddress() {
		return new DefaultIp6Address(lowestAddress(this.address.getBinaryAddress(), this.mask));
	}

	@Override
	public Ip6Address getHighestAddress() {
		return new DefaultIp6Address(highestAddress(this.address.getBinaryAddress(), this.mask));
	}

	@Override
	public boolean contains(final IpAddress<?> address) {
		if (!(address instanceof Ip6Address))
			return false;
		return equalsByMask(this.address.getBinaryAddress(), address.getBinaryAddress(), this.mask);
	}

	@Override
	public Collection<IpNet6Address> splitForMask(final Ip6Address lastSubnet, final short newMask, final int count) {
		return _splitForMask(IpNetworkUtils::createIpNet6Address, lastSubnet, newMask, count);
	}
}
