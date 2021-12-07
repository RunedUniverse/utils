/*
 * Copyright © 2021 Pl4yingNight (pl4yingnight@gmail.com)
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
package net.runeduniverse.lib.utils.logging.test.logs;

import java.util.ArrayList;
import java.util.List;

public class CompoundTree implements IEntry {

	protected final List<IEntry> entries = new ArrayList<>();
	protected final CharSequence tag;
	protected final CharSequence title;

	public CompoundTree() {
		this(null, null);
	}

	public CompoundTree(CharSequence title) {
		this(null, title);
	}

	public CompoundTree(CharSequence tag, CharSequence title) {
		this.tag = tag;
		this.title = title;
	}

	public CompoundTree append(CharSequence line) {
		this.entries.add(new LineEntry(line));
		return this;
	}

	public CompoundTree append(CharSequence tag, CharSequence line) {
		this.entries.add(new LineEntry(tag, line));
		return this;
	}

	public CompoundTree append(CompoundTree tree) {
		this.entries.add(tree);
		return this;
	}

	@Override
	public void toRecord(TreeRecord rootRecord) {
		TreeRecord record = new TreeRecord(this.tag, this.title);
		for (IEntry e : entries)
			e.toRecord(record);
		rootRecord.append(record);
	}

	@Override
	public String toString() {
		TreeRecord record = new TreeRecord(this.tag, this.title);
		for (IEntry e : entries)
			e.toRecord(record);
		return record.toString();
	}
}
