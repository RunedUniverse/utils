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

import java.util.LinkedList;
import java.util.List;

import net.runeduniverse.lib.utils.net.api.IpAddress;

public class IpBinaryUtils {

	private IpBinaryUtils() {
	}

	// ------------------------------------------------------------------------
	// GENERIC
	// ------------------------------------------------------------------------

	public static byte[] copyData(final byte[] data) {
		final byte[] copy = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			copy[i] = data[i];
		}
		return copy;
	}

	public static String toCidrNotation(final byte[] data, final String delimiter, final int segmentSize,
			final int radix) {
		final int size = data.length / segmentSize;
		final List<String> list = new LinkedList<>();
		for (int i = 0; i < size;) {
			String s = "";
			for (int j = 0; j < segmentSize && i < size; j++) {
				s = s + Integer.toString(Byte.toUnsignedInt(data[i++]), radix);
			}
			list.add(s);
		}
		return String.join(delimiter, list);
	}

	private static String trimToNull(String value) {
		if (value == null)
			return null;
		value = value.trim();
		if (value.isEmpty())
			return null;
		return value;
	}

	// ------------------------------------------------------------------------
	// IP Address
	// ------------------------------------------------------------------------

	// increment out of range => result.length == 0
	public static byte[] increment(final byte[] data) {
		final int length = data.length;
		final byte[] copy = copyData(data);

		for (int i = length - 1; 0 <= i; i--) {
			if ((copy[i] & 0xff) == 0xff) {
				copy[i] = 0;
			} else {
				copy[i] = (byte) (copy[i] + 0x01);
				return copy;
			}
		}
		return new byte[0];
	}

	// all missing segments at the end will be handles as being of value 0
	public static <T extends IpAddress<T>> int compareTo(final T address, final T ohterAddress) {
		// null check => null < value
		if (address == null) {
			if (ohterAddress == null)
				return 0;
			return -1;
		}
		if (ohterAddress == null)
			return 1;

		return compareTo(address.getBinaryAddress(), ohterAddress.getBinaryAddress());
	}

	// all missing segments at the end will be handles as being of value 0
	public static int compareTo(final byte[] data, final byte[] otherData) {
		final int maxLenght = (data.length < otherData.length) ? otherData.length : data.length;

		for (int i = 0; i < maxLenght; i++) {
			final short oct = (short) (i < data.length ? data[i] & 0xff : 0);
			final short otherOct = (short) (i < otherData.length ? otherData[i] & 0xff : 0);

			if (oct < otherOct)
				return -1;
			if (oct > otherOct)
				return 1;
		}
		return 0;
	}

	public static byte[] parseIp4Address(String ip) {
		if ((ip = trimToNull(ip)) == null)
			return null;
		final String[] segments = ip.split("\\.");
		if (segments.length == 4) {
			final byte[] address = new byte[4];
			try {
				for (int i = 0; i < address.length; i++) {
					address[i] = (byte) Integer.parseInt(segments[i], 10);
				}
				return address;
			} catch (NumberFormatException ignored) {
			}
		}
		return null;
	}

	public static byte[] parseIp6Address(String ip) {
		if ((ip = trimToNull(ip)) == null)
			return null;

		final String[] halves = ip.split("::", -1);
		// ERR: too many ::
		if (2 < halves.length)
			return null;

		final byte[] address = new byte[16];
		int pos = 0;

		try {
			if (halves.length == 2) {
				pos = parseHextets(halves[0], address, 0);

				byte[] tail = new byte[16];
				int tailLen = parseHextets(halves[1], tail, 16);
				int zeros = 16 - pos - tailLen;
				System.arraycopy(tail, 16 - tailLen, address, pos + zeros, tailLen);
			} else {
				// ERR: invalid length
				if (parseHextets(ip, address, 0) != 16)
					return null;
			}
			return address;
		} catch (NumberFormatException ignored) {
			return null;
		}
	}

	private static int parseHextets(final String part, final byte[] address, int offset) throws NumberFormatException {
		if (part.isEmpty())
			return 0;

		final String[] tokens = part.split(":");
		int pos = offset;

		for (String t : tokens) {
			if (t.contains(".")) {
				byte[] v4 = parseIp4Address(t);
				System.arraycopy(v4, 0, address, pos, 4);
				pos += 4;
			} else {
				int val = Integer.parseInt(t, 16);
				address[pos++] = (byte) (val >> 8);
				address[pos++] = (byte) (val);
			}
		}
		return pos - offset;
	}

	// ------------------------------------------------------------------------
	// IP Network
	// ------------------------------------------------------------------------

	public static byte[] lowestAddress(final byte[] address, final short mask) {
		final byte[] lower = copyData(address);
		final int segIndex = mask / 8;
		if (segIndex == lower.length)
			return lower;

		lower[segIndex] = (byte) (lower[segIndex] & ~(0xff >>> (mask % 8)));

		for (int i = segIndex + 1; i < lower.length; i++) {
			// set unsigned = 0x00
			lower[i] = 0x00;
		}
		return lower;
	}

	public static byte[] highestAddress(final byte[] address, final short mask) {
		final byte[] upper = copyData(address);
		final int segIndex = mask / 8;
		if (segIndex == upper.length)
			return upper;

		upper[segIndex] = (byte) (upper[segIndex] | (0xff >>> (mask % 8)));

		for (int i = segIndex + 1; i < upper.length; i++) {
			// set unsigned = 0xff
			upper[i] = ~0x00;
		}
		return upper;
	}

	public static boolean equalsByMask(final byte[] address0, final byte[] address1, final short mask) {
		if (address0.length != address1.length || (address0.length * 8) < mask)
			return false;
		final int segIndex = mask / 8;

		// first check all complete bytes
		// ex: mask = 21
		// ex: do for 2 bytes
		for (int i = 0; i < segIndex; i++) {
			if (address0[i] != address1[i])
				return false;
		}
		// now check the remaining bytes
		// ex: do for 5 bits (= 21 - 2*8)
		// ex: bitmask = 1111 1000
		final byte bitmask = (byte) (~(0xff >>> (mask % 8)));
		final byte b0 = (byte) (address0[segIndex] & bitmask);
		final byte b1 = (byte) (address1[segIndex] & bitmask);
		return b0 == b1;
	}
}
