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

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import net.runeduniverse.lib.utils.logging.log.api.CompoundTreeStyle;
import net.runeduniverse.lib.utils.logging.log.api.LineRecord;

public class SortedTreeRecord extends DefaultTreeRecord {

	protected final Comparator<LineRecord> comparator;

	public SortedTreeRecord(final CompoundTreeStyle style, final CharSequence title,
			final Comparator<LineRecord> comparator) {
		super(style, title);
		this.comparator = comparator;
	}

	public SortedTreeRecord(final CompoundTreeStyle style, final CharSequence tag, final CharSequence title,
			final Comparator<LineRecord> comparator) {
		super(style, tag, title);
		this.comparator = comparator;
	}

	public SortedTreeRecord(final CompoundTreeStyle style, final CharSequence title,
			final Supplier<List<LineRecord>> subRecordsSupplier, final Comparator<LineRecord> comparator) {
		super(style, title, subRecordsSupplier);
		this.comparator = comparator;
	}

	public SortedTreeRecord(final CompoundTreeStyle style, final CharSequence tag, final CharSequence title,
			final Supplier<List<LineRecord>> subRecordsSupplier, final Comparator<LineRecord> comparator) {
		super(style, tag, title, subRecordsSupplier);
		this.comparator = comparator;
	}

	@Override
	public SortedTreeRecord append(final LineRecord record) {
		super.append(record);
		return this;
	}

	@Override
	protected Iterator<LineRecord> subRecordIterator() {
		return this.subRecords.stream()
				.sorted(this.comparator)
				.iterator();
	}

}
