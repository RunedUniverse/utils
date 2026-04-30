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

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.runeduniverse.lib.utils.net.api.IpAddress;
import net.runeduniverse.lib.utils.net.api.Ip4Address;
import net.runeduniverse.lib.utils.net.api.IpNet4Address;
import net.runeduniverse.lib.utils.net.api.Ip6Address;
import net.runeduniverse.lib.utils.net.api.IpNet6Address;
import net.runeduniverse.lib.utils.net.api.IpNetAddress;

public class IpNetworkUtils {

	private IpNetworkUtils() {
	}

	public static Ip4Address createIp4Address() {
		return new DefaultIp4Address();
	}

	public static Ip4Address createIp4Address(final byte[] address) {
		if (address.length != 4)
			return null;
		return new DefaultIp4Address(address);
	}

	public static IpNet4Address createIpNet4Address() {
		return new DefaultIpNet4Address(createIp4Address(), (short) 32);
	}

	public static IpNet4Address createIpNet4Address(final byte[] address, final short mask) {
		return createIpNet4Address(createIp4Address(address), mask);
	}

	public static IpNet4Address createIpNet4Address(final Ip4Address address, final short mask) {
		if (mask < 0 || 32 < mask)
			return null;
		return new DefaultIpNet4Address(address, mask);
	}

	public static Ip6Address createIp6Address() {
		return new DefaultIp6Address();
	}

	public static Ip6Address createIp6Address(final byte[] address) {
		if (address.length != 16)
			return null;
		return new DefaultIp6Address(address);
	}

	public static IpNet6Address createIpNet6Address() {
		return new DefaultIpNet6Address(createIp6Address(), (short) 128);
	}

	public static IpNet6Address createIpNet6Address(final byte[] address, final short mask) {
		return createIpNet6Address(createIp6Address(address), mask);
	}

	public static IpNet6Address createIpNet6Address(final Ip6Address address, final short mask) {
		if (mask < 0 || 128 < mask)
			return null;
		return new DefaultIpNet6Address(address, mask);
	}

	public static Ip4Address parseIp4Address(final String ip) throws UnknownHostException {
		final Ip4Address result = tryParseIp4Address(ip);
		if (result == null)
			throw new UnknownHostException("Ipv4 Address<" + ip + "> can not be parsed!");
		return result;
	}

	public static Ip4Address tryParseIp4Address(final String ip) {
		final byte[] address = IpBinaryUtils.parseIp4Address(ip);
		if (address == null)
			return null;
		return new DefaultIp4Address(address);
	}

	public static IpNet4Address parseIpNet4Address(final String subnet) throws UnknownHostException {
		final IpNet4Address result = tryParseIpNet4Address(subnet);
		if (result == null)
			throw new UnknownHostException("Ipv4 Address (Subnet) <" + subnet + "> can not be parsed!");
		return result;
	}

	public static IpNet4Address tryParseIpNet4Address(final String subnet) {
		if (subnet == null)
			return null;
		final String[] segments = subnet.trim()
				.split("/", -1);
		if (segments.length == 1) {
			final Ip4Address ip = tryParseIp4Address(segments[0]);
			if (ip == null)
				return null;
			return new DefaultIpNet4Address(ip);
		}
		try {
			if (segments.length == 2) {
				short mask = Short.parseShort(segments[1], 10);
				if (0 <= mask && mask <= 32) {
					final Ip4Address ip = tryParseIp4Address(segments[0]);
					if (ip == null)
						return null;
					return new DefaultIpNet4Address(ip, mask);
				}
			}
		} catch (NumberFormatException e) {
		}
		return null;
	}

	public static Ip6Address parseIp6Address(final String ip) throws UnknownHostException {
		final Ip6Address result = tryParseIp6Address(ip);
		if (result == null)
			throw new UnknownHostException("Ipv6 Address<" + ip + "> can not be parsed!");
		return result;
	}

	public static Ip6Address tryParseIp6Address(final String ip) {
		final byte[] address = IpBinaryUtils.parseIp6Address(ip);
		if (address == null)
			return null;
		return new DefaultIp6Address(address);
	}

	public static IpNet6Address parseIpNet6Address(final String subnet) throws UnknownHostException {
		final IpNet6Address result = tryParseIpNet6Address(subnet);
		if (result == null)
			throw new UnknownHostException("Ipv6 Address (Subnet) <" + subnet + "> can not be parsed!");
		return result;
	}

	public static IpNet6Address tryParseIpNet6Address(final String subnet) {
		if (subnet == null)
			return null;
		final String[] segments = subnet.trim()
				.split("/", -1);
		if (segments.length == 1) {
			final Ip6Address ip = tryParseIp6Address(segments[0]);
			if (ip == null)
				return null;
			return new DefaultIpNet6Address(ip);
		}
		try {
			if (segments.length == 2) {
				short mask = Short.parseShort(segments[1], 10);
				if (0 <= mask && mask <= 32) {
					final Ip6Address ip = tryParseIp6Address(segments[0]);
					if (ip == null)
						return null;
					return new DefaultIpNet6Address(ip, mask);
				}
			}
		} catch (NumberFormatException e) {
		}
		return null;
	}

	protected static <T extends IpAddress<T>> Collection<T> _interpolateIpRange(final T ip1, final T ip2,
			final Function<byte[], T> factory) {
		final List<T> list = new LinkedList<>();
		final T lowerIp;
		final T upperIp;

		switch (ip1.compareTo(ip2)) {
		case -1:
			lowerIp = ip1;
			upperIp = ip2;
			break;
		case 1:
			lowerIp = ip2;
			upperIp = ip1;
			break;
		case 0:
			list.add(ip1);
		default:
			// in case of default,
			// which should never happen
			// => empty list
			return list;
		}

		final byte[] lowerData = lowerIp.getBinaryAddress();
		final byte[] upperData = upperIp.getBinaryAddress();
		byte[] data = lowerData;

		do {
			// 1st round adds lowerIp (initial data value)
			list.add(factory.apply(data));
			data = IpBinaryUtils.increment(data);
			if (data.length == 0) {
				// increment out of range!
				// should never happen ...
				break;
			}
		} while (IpBinaryUtils.compareTo(data, upperData) < 0);
		// add last element
		list.add(upperIp);

		return list;
	}

	public static Collection<Ip4Address> interpolateIpRange(final Ip4Address ip1, final Ip4Address ip2) {
		return _interpolateIpRange(ip1, ip2, DefaultIp4Address::new);
	}

	public static Collection<Ip4Address> interpolateIpRange(final IpNet4Address subnet) {
		return interpolateIpRange(subnet.getLowestAddress(), subnet.getHighestAddress());
	}

	public static Collection<Ip6Address> interpolateIpRange(final Ip6Address ip1, final Ip6Address ip2) {
		return _interpolateIpRange(ip1, ip2, DefaultIp6Address::new);
	}

	public static Collection<Ip6Address> interpolateIpRange(final IpNet6Address subnet) {
		return interpolateIpRange(subnet.getLowestAddress(), subnet.getHighestAddress());
	}

	protected static <IP extends IpAddress<IP>, NET extends IpNetAddress<IP, NET>> Collection<NET> _extractIpSubnets(
			final Collection<IP> addresses, final short fullMask, final BiFunction<IP, Short, NET> factory) {
		final Set<NET> results = new LinkedHashSet<>();
		final List<NET> subnets = new LinkedList<>();
		final SortedSet<NET> next = new TreeSet<>();

		// prime the algorithm
		// ensure that next does not contain 'null' elements!
		addresses.stream()
				.filter(Objects::nonNull)
				.distinct()
				.map(ip -> factory.apply(ip, fullMask))
				.filter(Objects::nonNull)
				.forEach(next::add);

		// start extraction
		for (short mask = (short) (fullMask - 1); 0 <= mask && !next.isEmpty(); mask--) {
			// reset collections
			subnets.clear();
			// ascending order required so that equivalent by mask (-1) binary addresses are
			// next too each other
			subnets.addAll(next);
			next.clear();
			// consolidate by most matching bits
			for (ListIterator<NET> i = subnets.listIterator(); i.hasNext();) {
				final NET subnet = i.next();
				// validate
				if (i.hasNext() == false) {
					results.add(subnet);
					break;
				}
				// check if the 2 subnets are equivalent and can be combined
				// 10.0.0.00001010/32 & 10.0.0.00001011/32 = 10.0.0.00001010/31
				final NET nextSubnet = i.next();
				if (subnet.equals(nextSubnet, mask)) {
					next.add(factory.apply(subnet.getLowestAddress(), mask));
					continue;
				}
				results.add(subnet);
				// reset iterator in case of no match
				i.previous();
			}
		}
		// just in case the addresses collection did indeed contain every possible
		// address, also add the remaining subnet to the results
		results.addAll(next);

		return Collections.unmodifiableCollection(results);
	}

	public static Collection<IpNet4Address> extractIp4Subnets(final Collection<Ip4Address> addresses) {
		return IpNetworkUtils.<Ip4Address, IpNet4Address>_extractIpSubnets(addresses, (short) 32,
				IpNetworkUtils::createIpNet4Address);
	}

	public static Collection<IpNet6Address> extractIp6Subnets(final Collection<Ip6Address> addresses) {
		return IpNetworkUtils.<Ip6Address, IpNet6Address>_extractIpSubnets(addresses, (short) 128,
				IpNetworkUtils::createIpNet6Address);
	}

	public static Collection<IpNetAddress<?, ?>> extractIpSubnets(final Collection<IpAddress<?>> addresses) {
		final Set<Ip4Address> ip4s = new LinkedHashSet<>();
		final Set<Ip6Address> ip6s = new LinkedHashSet<>();

		filter(addresses, ip4s, ip6s);

		// extract the subnets
		final Set<IpNetAddress<?, ?>> subnets = new LinkedHashSet<>();
		subnets.addAll(extractIp4Subnets(ip4s));
		try {
			subnets.addAll(extractIp6Subnets(ip6s));
		} catch (UnsupportedOperationException e) {
			// ipv6 currently out of scope!
		}
		return subnets;
	}

	protected static <IP extends IpAddress<IP>> Collection<Deque<IP>> _extractIpRanges(final Collection<IP> addresses) {
		final List<IP> list;
		if (addresses instanceof List<?>) {
			list = (List<IP>) addresses;
		} else {
			list = new LinkedList<IP>(addresses);
		}
		// Step 1: sort
		list.sort(null);

		final List<Deque<IP>> resultList = new LinkedList<>();
		// Step 2: detect the gaps & group ranges
		final Deque<IP> que = new LinkedList<>();
		byte[] data = new byte[0];
		for (IP ip : list) {
			// make sure data has a valid data entry
			if (data.length == 0) {
				que.add(ip);
				data = ip.getBinaryAddress();
				continue;
			}
			// increment to the next expected ipAddress
			data = IpBinaryUtils.increment(data);
			if (data.length == 0) {
				// done / data out of range!
				break;
			}

			if (IpBinaryUtils.compareTo(data, ip.getBinaryAddress()) == 0) {
				// ipAddress is still an increment -> add it to queue
				que.add(ip);
				continue;
			}

			// Step 3.1: sanitize the range
			// gap detected => sanitize queue and add it to result
			final Deque<IP> result = new LinkedList<>();
			result.add(que.getFirst());
			final IP lastIp = que.getLast();
			if (!result.contains(lastIp))
				result.add(lastIp);
			// result is reduced to 1-2 distinct values
			resultList.add(result);
			// reset queue for next iteration
			que.clear();
			que.add(ip);
			data = ip.getBinaryAddress();
		}

		// Step 3.2: sanitize the last range
		// sanitize the last queue and add it to result
		final Deque<IP> result = new LinkedList<>();
		result.add(que.getFirst());
		final IP lastIp = que.getLast();
		if (!result.contains(lastIp))
			result.add(lastIp);
		// result is reduced to 1-2 distinct values
		resultList.add(result);
		return resultList;
	}

	public static Collection<Deque<Ip4Address>> extractIp4Ranges(final Collection<Ip4Address> addresses) {
		return _extractIpRanges(addresses);
	}

	public static Collection<Deque<Ip6Address>> extractIp6Ranges(final Collection<Ip6Address> addresses) {
		return _extractIpRanges(addresses);
	}

	public static Collection<Deque<IpAddress<?>>> extractIpRanges(final Collection<IpAddress<?>> addresses) {
		final Collection<Ip4Address> ipv4Col = new LinkedList<>();
		final Collection<Ip6Address> ipv6Col = new LinkedList<>();

		filter(addresses, ipv4Col, ipv6Col);

		final List<Deque<IpAddress<?>>> list = new LinkedList<>();
		for (Deque<Ip4Address> deque : extractIp4Ranges(ipv4Col)) {
			list.add(new LinkedList<IpAddress<?>>(deque));
		}
		for (Deque<Ip6Address> deque : extractIp6Ranges(ipv6Col)) {
			list.add(new LinkedList<IpAddress<?>>(deque));
		}
		return list;
	}

	protected static void filter(final Collection<IpAddress<?>> addresses, final Collection<Ip4Address> ipv4Col,
			final Collection<Ip6Address> ipv6Col) {
		for (IpAddress<?> ip : addresses) {
			if (ip instanceof Ip4Address)
				ipv4Col.add((Ip4Address) ip);
			else if (ip instanceof Ip6Address)
				ipv6Col.add((Ip6Address) ip);
		}
	}
}
