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
import net.runeduniverse.lib.utils.logging.log.api.LineRecord;
import net.runeduniverse.lib.utils.logging.log.api.LogEntry;
import net.runeduniverse.lib.utils.logging.log.api.TreeRecord;

public class SortedCompoundTree extends DefaultCompoundTree {

	public SortedCompoundTree() {
		super(null, null, null);
	}

	public SortedCompoundTree(final CompoundTreeStyle style) {
		super(null, null, style);
	}

	public SortedCompoundTree(final CharSequence title) {
		super(null, title, null);
	}

	public SortedCompoundTree(final CharSequence title, final CompoundTreeStyle style) {
		super(null, title, style);
	}

	public SortedCompoundTree(final CharSequence tag, final CharSequence title) {
		super(tag, title, null);
	}

	public SortedCompoundTree(final CharSequence tag, final CharSequence title, final CompoundTreeStyle style) {
		super(tag, title, style, ArrayList::new);
	}

	public SortedCompoundTree(final CharSequence tag, final CharSequence title, final CompoundTreeStyle style,
			final Supplier<List<LogEntry>> entryListSupplier) {
		super(tag, title, style, entryListSupplier);
	}

	public SortedCompoundTree append(final CharSequence line) {
		super.append(line);
		return this;
	}

	public SortedCompoundTree append(final CharSequence tag, final CharSequence line) {
		super.append(tag, line);
		return this;
	}

	public SortedCompoundTree append(final CompoundTree tree) {
		super.append(tree);
		return this;
	}

	@Override
	protected TreeRecord toRecord() {
		return new SortedTreeRecord(this.style, this.tag, this.title, this::compare);
	}

	protected int compare(final LineRecord r0, final LineRecord r1) {
		if (r0 == r1)
			return 0;
		if (r0 == null)
			return -1;
		if (r1 == null)
			return 1;
		final CharSequence tag0 = r0.getTag(), tag1 = r1.getTag();
		if (tag0 == tag1)
			return 0;
		if (tag0 == null)
			return -1;
		if (tag1 == null)
			return 1;
		return compare(tag0, tag1);
	}

	protected int compare(final CharSequence tag0, final CharSequence tag1) {
		return tag0.toString()
				.compareTo(tag1.toString());
	}
}
