/*
 * Copyright © 2024 VenaNocta (venanocta@gmail.com)
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
package net.runeduniverse.lib.utils.logging.logs;

public interface CompoundTreeBOM {

	public static CharSequence INITIAL_OFFSET = "  ";
	public static CharSequence NO_ELEMENT_OFFSET = "   ";
	public static CharSequence EMPTY_ELEMENT_OFFSET = " │ ";
	public static CharSequence ELEMENT_OFFSET = " ├ ";
	public static CharSequence LAST_ELEMENT_OFFSET = " └ ";

	public static CharSequence TAGGED_TXT_BOX = "[%s]";
	public static CharSequence TAGGED_TXT_SPLITTER = " » ";

	public static boolean TAGS_TO_UPPER = true;
}
