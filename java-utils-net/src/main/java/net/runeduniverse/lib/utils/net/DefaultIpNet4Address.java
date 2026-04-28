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

import java.util.Collection;

import net.runeduniverse.lib.utils.net.api.Ip4Address;
import net.runeduniverse.lib.utils.net.api.IpAddress;
import net.runeduniverse.lib.utils.net.api.IpNet4Address;
import net.runeduniverse.lib.utils.net.api.IpNetAddress;

public class DefaultIpNet4Address extends AIpNetAddress<Ip4Address, IpNet4Address> implements IpNet4Address {

	public DefaultIpNet4Address(final Ip4Address address) {
		this(address, (short) 32);
	}

	public DefaultIpNet4Address(final Ip4Address address, final short mask) {
		super(address, mask);
	}

	@Override
	protected boolean isApplicable(final IpNetAddress<?, ?> instance) {
		return instance instanceof IpNet4Address;
	}

	@Override
	public IpNet4Address copy() {
		return new DefaultIpNet4Address(this.address, this.mask);
	}

	@Override
	public Ip4Address getLowestAddress() {
		return new DefaultIp4Address(lowestAddress(this.address.getBinaryAddress(), this.mask));
	}

	@Override
	public Ip4Address getHighestAddress() {
		return new DefaultIp4Address(highestAddress(this.address.getBinaryAddress(), this.mask));
	}

	@Override
	public boolean contains(final IpAddress<?> address) {
		if (!(address instanceof Ip4Address))
			return false;
		return equalsByMask(this.address.getBinaryAddress(), address.getBinaryAddress(), this.mask);
	}

	@Override
	public Collection<IpNet4Address> splitForMask(final Ip4Address lastSubnet, final short newMask, final int count) {
		return _splitForMask(IpNetworkUtils::createIpNet4Address, lastSubnet, newMask, count);
	}
}
