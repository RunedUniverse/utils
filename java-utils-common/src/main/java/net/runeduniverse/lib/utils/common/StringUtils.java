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
package net.runeduniverse.lib.utils.common;

public class StringUtils {

	public static boolean isEmpty(final String s) {
		return s == null || s.length() == 0;
	}

	public static boolean isBlank(final String s) {
		// Null-safe, short-circuit evaluation.
		return s == null || s.trim()
				.isEmpty();
	}

	public static String trim(final String s) {
		return s == null ? null : s.trim();
	}

	public static String trimToNull(String s) {
		return isEmpty(s = trim(s)) ? null : s;
	}

	public static String strip(final String s, final String stripChars) {
		if (isEmpty(s))
			return s;
		return stripEnd(stripStart(s, stripChars), stripChars);
	}

	public static String stripStart(final String s, final String stripChars) {
		int strLen;
		if (s == null || (strLen = s.length()) == 0)
			return s;

		int start = 0;
		if (stripChars == null) {
			while ((start != strLen) && Character.isWhitespace(s.charAt(start)))
				start++;
		} else if (stripChars.length() == 0) {
			return s;
		} else {
			while ((start != strLen) && (stripChars.indexOf(s.charAt(start)) != -1))
				start++;
		}
		return s.substring(start);
	}

	public static String stripEnd(final String s, final String stripChars) {
		int end;
		if (s == null || (end = s.length()) == 0)
			return s;

		if (stripChars == null) {
			while ((end != 0) && Character.isWhitespace(s.charAt(end - 1)))
				end--;
		} else if (stripChars.length() == 0) {
			return s;
		} else {
			while ((end != 0) && (stripChars.indexOf(s.charAt(end - 1)) != -1))
				end--;
		}
		return s.substring(0, end);
	}

	public static boolean strEquals(final String a, final String b) {
		return ComparisonUtils.objectEquals(a, b);
	}

	public static boolean strEqualsIgnoreCase(final String a, final String b) {
		if (a == b)
			return true;
		if (a == null || b == null)
			return false;
		return a.equalsIgnoreCase(b);
	}
}
