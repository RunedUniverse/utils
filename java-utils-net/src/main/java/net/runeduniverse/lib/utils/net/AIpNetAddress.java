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
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import net.runeduniverse.lib.utils.net.api.IpAddress;
import net.runeduniverse.lib.utils.net.api.IpNetAddress;

public abstract class AIpNetAddress<A extends IpAddress<A>, T extends IpNetAddress<A, T>>
		implements IpNetAddress<A, T> {

	private static final long serialVersionUID = 1L;

	protected final A address;
	protected final short mask;

	protected AIpNetAddress(final A address, final short mask) {
		this.address = address;
		this.mask = mask;
	}

	@Override
	public A getBaseAddress() {
		return this.address;
	}

	public short getMask() {
		return this.mask;
	}

	protected abstract boolean isApplicable(final IpNetAddress<?, ?> instance);

	protected byte[] lowestAddress(final byte[] address, final short mask) {
		return IpBinaryUtils.lowestAddress(address, mask);
	}

	protected byte[] highestAddress(final byte[] address, final short mask) {
		return IpBinaryUtils.highestAddress(address, mask);
	}

	protected boolean equalsByMask(final byte[] address0, final byte[] address1, final short mask) {
		return IpBinaryUtils.equalsByMask(address0, address1, mask);
	}

	protected Collection<T> _splitForMask(final BiFunction<byte[], Short, T> factory, final A lastSubnet,
			final short newMask, final int count) {
		final byte[] rawData = getLowestAddress().getBinaryAddress();
		final byte[] lastNetData = lastSubnet == null ? null : lastSubnet.getBinaryAddress();
		return IpBinaryUtils.splitNetworkForMask(rawData, lastNetData, this.mask, newMask, count)
				.stream()
				.map(data -> factory.apply(data, newMask))
				.collect(Collectors.toList());
	}

	@Override
	public boolean equals(final IpNetAddress<?, ?> subnet, final short mask) {
		if (!isApplicable(subnet))
			return false;
		return equalsByMask(this.address.getBinaryAddress(), subnet.getBaseAddress()
				.getBinaryAddress(), mask);
	}

	@Override
	public boolean equals(final IpNetAddress<?, ?> subnet) {
		if (!isApplicable(subnet) || this.mask != subnet.getMask())
			return false;
		return equals(subnet, this.mask);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof IpNetAddress<?, ?>))
			return false;
		return equals((IpNetAddress<?, ?>) obj);
	}

	@Override
	public boolean isSupernetOf(final IpNetAddress<?, ?> subnet) {
		if (!isApplicable(subnet) || subnet.getMask() < this.mask)
			return false;
		return equalsByMask(this.address.getBinaryAddress(), subnet.getBaseAddress()
				.getBinaryAddress(), this.mask);
	}

	@Override
	public String toCidrNotation() {
		return getLowestAddress().toCidrNotation() + '/' + this.mask;
	}

	@Override
	public int hashCode() {
		return toCidrNotation().hashCode();
	}
}
