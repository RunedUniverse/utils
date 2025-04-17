/*
 * Copyright © 2025 VenaNocta (venanocta@gmail.com)
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
package net.runeduniverse.lib.utils.logging.log.api;

public interface CompoundTreeStyle {

	public static CharSequence DEFAULT_INITIAL_OFFSET = "  ";
	public static CharSequence DEFAULT_NO_ELEMENT_OFFSET = "   ";
	public static CharSequence DEFAULT_EMPTY_ELEMENT_OFFSET = " │ ";
	public static CharSequence DEFAULT_ELEMENT_OFFSET = " ├ ";
	public static CharSequence DEFAULT_LAST_ELEMENT_OFFSET = " └ ";

	public static CharSequence DEFAULT_TAGGED_TXT_BOX = "[%s]";
	public static CharSequence DEFAULT_TAGGED_TXT_SPLITTER = " » ";

	public static boolean DEFAULT_TAGS_TO_UPPER = true;

	public default CharSequence getInitialOffset() {
		return DEFAULT_INITIAL_OFFSET;
	}

	public default CharSequence getNoElementOffset() {
		return DEFAULT_NO_ELEMENT_OFFSET;
	}

	public default CharSequence getEmptyElementOffset() {
		return DEFAULT_EMPTY_ELEMENT_OFFSET;
	}

	public default CharSequence getElementOffset() {
		return DEFAULT_ELEMENT_OFFSET;
	}

	public default CharSequence getLastElementOffset() {
		return DEFAULT_LAST_ELEMENT_OFFSET;
	}

	public default CharSequence getTaggedTxtBox() {
		return DEFAULT_TAGGED_TXT_BOX;
	}

	public default CharSequence getTaggedTxtSplitter() {
		return DEFAULT_TAGGED_TXT_SPLITTER;
	}

	public default boolean getTagsToUpper() {
		return DEFAULT_TAGS_TO_UPPER;
	}
}
