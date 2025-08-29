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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import net.runeduniverse.lib.utils.logging.log.api.CompoundTreeStyle;
import net.runeduniverse.lib.utils.logging.log.api.LineRecord;
import net.runeduniverse.lib.utils.logging.log.api.TreeRecord;

public class DefaultTreeRecord extends DefaultLineRecord implements TreeRecord {

	protected final List<LineRecord> subRecords;
	protected int mxTagSize = 0;

	public DefaultTreeRecord(final CompoundTreeStyle style, final CharSequence title) {
		this(style, title, ArrayList::new);
	}

	public DefaultTreeRecord(final CompoundTreeStyle style, final CharSequence tag, final CharSequence title) {
		this(style, tag, title, ArrayList::new);
	}

	public DefaultTreeRecord(final CompoundTreeStyle style, final CharSequence title,
			final Supplier<List<LineRecord>> subRecordsSupplier) {
		super(style, title);
		this.subRecords = subRecordsSupplier.get();
	}

	public DefaultTreeRecord(final CompoundTreeStyle style, final CharSequence tag, final CharSequence title,
			final Supplier<List<LineRecord>> subRecordsSupplier) {
		super(style, tag, title);
		this.subRecords = subRecordsSupplier.get();
	}

	public DefaultTreeRecord append(final LineRecord record) {
		this.subRecords.add(record);
		int s;
		if ((s = record.getTagSize()) > this.mxTagSize)
			this.mxTagSize = s;
		return this;
	}

	protected Iterator<LineRecord> subRecordIterator() {
		return this.subRecords.stream()
				.iterator();
	}

	@Override
	public List<String> write(final CharSequence[] baseOffset, final int tagSize) {
		final List<String> result = new LinkedList<>();
		result.addAll(super.write(baseOffset, tagSize));
		final CharSequence[] offset = new CharSequence[baseOffset.length + 1];
		for (int i = 0; i < baseOffset.length; i++)
			offset[i] = baseOffset[i];
		if (baseOffset.length > 1) {
			if (baseOffset[baseOffset.length - 1] == this.style.getLastElementOffset())
				offset[baseOffset.length - 1] = this.style.getNoElementOffset();
			else if (baseOffset[baseOffset.length - 1] == this.style.getElementOffset())
				offset[baseOffset.length - 1] = this.style.getEmptyElementOffset();
		}

		for (Iterator<LineRecord> iterator = subRecordIterator(); iterator.hasNext();) {
			final LineRecord r = (LineRecord) iterator.next();
			if (iterator.hasNext())
				offset[baseOffset.length] = this.style.getElementOffset();
			else
				offset[baseOffset.length] = this.style.getLastElementOffset();
			result.addAll(r.write(offset, this.mxTagSize));
		}
		return result;
	}

	@Override
	public String toString() {
		return String.join("\n", write(new CharSequence[] { this.style.getInitialOffset() }, getTagSize()));
	}

	@Override
	public List<String> toText(final CharSequence prefix) {
		CharSequence offset = this.style.getInitialOffset();
		if (offset == null)
			offset = "";
		if (prefix != null)
			offset = prefix.toString() + offset.toString();
		return write(new CharSequence[] { offset }, getTagSize());
	}
}
