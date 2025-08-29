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
import java.util.function.Supplier;

import net.runeduniverse.lib.utils.logging.log.api.CompoundTree;
import net.runeduniverse.lib.utils.logging.log.api.CompoundTreeStyle;
import net.runeduniverse.lib.utils.logging.log.api.LogEntry;
import net.runeduniverse.lib.utils.logging.log.api.TreeRecord;

public class DefaultCompoundTree implements CompoundTree {

	protected final CompoundTreeStyle style;
	protected final List<LogEntry> entries;
	protected final CharSequence tag;
	protected final CharSequence title;

	public DefaultCompoundTree() {
		this(null, null, null);
	}

	public DefaultCompoundTree(final CompoundTreeStyle style) {
		this(null, null, style);
	}

	public DefaultCompoundTree(final CharSequence title) {
		this(null, title, null);
	}

	public DefaultCompoundTree(final CharSequence title, final CompoundTreeStyle style) {
		this(null, title, style);
	}

	public DefaultCompoundTree(final CharSequence tag, final CharSequence title) {
		this(tag, title, null);
	}

	public DefaultCompoundTree(final CharSequence tag, final CharSequence title, final CompoundTreeStyle style) {
		this(tag, title, style, ArrayList::new);
	}

	public DefaultCompoundTree(final CharSequence tag, final CharSequence title, final CompoundTreeStyle style,
			final Supplier<List<LogEntry>> entryListSupplier) {
		this.style = style == null ? defaultBOM() : style;
		this.entries = entryListSupplier.get();
		this.tag = tag;
		this.title = title;
	}

	@Override
	public boolean hasLines() {
		return checkContent(true, false);
	}

	@Override
	public boolean hasSubTrees() {
		return checkContent(false, true);
	}

	@Override
	public boolean hasContent() {
		return checkContent(true, true);
	}

	protected boolean checkContent(final boolean matchLines, final boolean matchTrees) {
		for (LogEntry entry : this.entries) {
			// check CompoundTree
			if (entry instanceof CompoundTree) {
				if (matchTrees)
					return true;
				continue;
			}
			// normal entry
			if (matchLines)
				return true;
		}
		return false;
	}

	public DefaultCompoundTree append(final CharSequence line) {
		this.entries.add(new LineEntry(this.style, line));
		return this;
	}

	public DefaultCompoundTree append(final CharSequence tag, final CharSequence line) {
		this.entries.add(new LineEntry(this.style, tag, line));
		return this;
	}

	public DefaultCompoundTree append(final CompoundTree tree) {
		this.entries.add(tree);
		return this;
	}

	protected TreeRecord toRecord() {
		return new DefaultTreeRecord(this.style, this.tag, this.title);
	}

	protected TreeRecord toRecord2() {
		final TreeRecord record = toRecord();
		for (LogEntry e : this.entries)
			e.toRecord(record);
		return record;
	}

	@Override
	public void toRecord(final TreeRecord rootRecord) {
		rootRecord.append(toRecord2());
	}

	@Override
	public String toString() {
		return toRecord2().toString();
	}

	@Override
	public List<String> toText() {
		return toText(null);
	}

	@Override
	public List<String> toText(final CharSequence prefix) {
		return toRecord2().toText(prefix);
	}

	public static CompoundTreeStyle defaultBOM() {
		return new CompoundTreeStyle() {
		};
	}
}
