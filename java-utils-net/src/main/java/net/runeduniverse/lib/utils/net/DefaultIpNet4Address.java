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

import net.runeduniverse.lib.utils.net.api.IpAddress;
import net.runeduniverse.lib.utils.net.api.Ip4Address;
import net.runeduniverse.lib.utils.net.api.IpNetAddress;
import net.runeduniverse.lib.utils.net.api.IpNet4Address;

public class DefaultIpNet4Address extends AIpNetAddress<Ip4Address, IpNet4Address> implements IpNet4Address {

	private static final long serialVersionUID = 1L;

	public DefaultIpNet4Address(final Ip4Address address) {
		this(address, (short) 32);
	}

	public DefaultIpNet4Address(final Ip4Address address, final short mask) {
		super(address, mask);
	}

	protected IpNet4Address _new(final Ip4Address address, final short mask) {
		return new DefaultIpNet4Address(address, mask);
	}

	protected final IpNet4Address _new(final byte[] data, final short mask) {
		return _new(_newIp(data), mask);
	}

	protected Ip4Address _newIp(final byte[] data) {
		return new DefaultIp4Address(data);
	}

	@Override
	protected boolean isApplicable(final IpNetAddress<?, ?> instance) {
		return instance instanceof IpNet4Address;
	}

	@Override
	public IpNet4Address copy() {
		return _new(this.address, this.mask);
	}

	@Override
	public Ip4Address getLowestAddress() {
		return _newIp(lowestAddress(this.address.getBinaryAddress(), this.mask));
	}

	@Override
	public Ip4Address getHighestAddress() {
		return _newIp(highestAddress(this.address.getBinaryAddress(), this.mask));
	}

	@Override
	public int compareTo(final IpNet4Address o) {
		return IpNetAddress.compare(this, o);
	}

	@Override
	public boolean contains(final IpAddress<?> address) {
		if (!(address instanceof Ip4Address))
			return false;
		return equalsByMask(this.address.getBinaryAddress(), address.getBinaryAddress(), this.mask);
	}

	@Override
	public Collection<IpNet4Address> splitForMask(final Ip4Address lastSubnet, final short newMask, final int count) {
		return _splitForMask(this::_new, lastSubnet, newMask, count);
	}
}
