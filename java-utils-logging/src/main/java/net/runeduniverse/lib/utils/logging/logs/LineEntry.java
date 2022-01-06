/*
 * Copyright © 2022 Pl4yingNight (pl4yingnight@gmail.com)
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

public class LineEntry implements IEntry {

	protected CharSequence tag;
	protected CharSequence content;

	public LineEntry(CharSequence content) {
		this(null, content);
	}

	public LineEntry(CharSequence tag, CharSequence content) {
		this.tag = tag;
		this.content = content;
	}

	@Override
	public void toRecord(TreeRecord record) {
		record.append(new LineRecord(this.tag, this.content));
	}

}
