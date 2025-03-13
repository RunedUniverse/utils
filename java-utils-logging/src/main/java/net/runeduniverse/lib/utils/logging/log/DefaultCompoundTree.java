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
package net.runeduniverse.lib.utils.logging.log;

import java.util.ArrayList;
import java.util.List;

import net.runeduniverse.lib.utils.logging.log.api.CompoundTree;
import net.runeduniverse.lib.utils.logging.log.api.LogEntry;
import net.runeduniverse.lib.utils.logging.log.api.TreeRecord;

public class DefaultCompoundTree implements CompoundTree {

	protected final List<LogEntry> entries = new ArrayList<>();
	protected final CharSequence tag;
	protected final CharSequence title;

	public DefaultCompoundTree() {
		this(null, null);
	}

	public DefaultCompoundTree(final CharSequence title) {
		this(null, title);
	}

	public DefaultCompoundTree(final CharSequence tag, final CharSequence title) {
		this.tag = tag;
		this.title = title;
	}

	public DefaultCompoundTree append(final CharSequence line) {
		this.entries.add(new LineEntry(line));
		return this;
	}

	public DefaultCompoundTree append(final CharSequence tag, final CharSequence line) {
		this.entries.add(new LineEntry(tag, line));
		return this;
	}

	public DefaultCompoundTree append(final CompoundTree tree) {
		this.entries.add(tree);
		return this;
	}

	@Override
	public void toRecord(final TreeRecord rootRecord) {
		final DefaultTreeRecord record = new DefaultTreeRecord(this.tag, this.title);
		for (LogEntry e : entries)
			e.toRecord(record);
		rootRecord.append(record);
	}

	@Override
	public String toString() {
		final DefaultTreeRecord record = new DefaultTreeRecord(this.tag, this.title);
		for (LogEntry e : entries)
			e.toRecord(record);
		return record.toString();
	}
}
