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
import java.util.List;
import java.util.function.Supplier;

import net.runeduniverse.lib.utils.logging.log.api.CompoundTreeStyle;
import net.runeduniverse.lib.utils.logging.log.api.LineRecord;
import net.runeduniverse.lib.utils.logging.log.api.TreeRecord;

public class DefaultTreeRecord extends DefaultLineRecord implements TreeRecord {

	private final List<LineRecord> subRecords;
	private int mxTagSize = 0;

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

	@Override
	public String write(final CharSequence[] baseOffset, final int tagSize) {
		final StringBuilder builder = new StringBuilder(String.join("", baseOffset)).append(this.buildLine(tagSize));
		final CharSequence[] offset = new CharSequence[baseOffset.length + 1];
		for (int i = 0; i < baseOffset.length; i++)
			offset[i] = baseOffset[i];
		if (baseOffset.length > 1) {
			if (baseOffset[baseOffset.length - 1] == this.style.getElementOffset())
				offset[baseOffset.length - 1] = this.style.getEmptyElementOffset();
			if (baseOffset[baseOffset.length - 1] == this.style.getLastElementOffset())
				offset[baseOffset.length - 1] = this.style.getNoElementOffset();
		}

		for (Iterator<LineRecord> iterator = subRecords.iterator(); iterator.hasNext();) {
			final LineRecord r = (LineRecord) iterator.next();
			if (iterator.hasNext())
				offset[baseOffset.length] = this.style.getElementOffset();
			else
				offset[baseOffset.length] = this.style.getLastElementOffset();
			builder.append(r.write(offset, this.mxTagSize));
		}
		return builder.toString();
	}

	@Override
	public String toString() {
		return this.write(new CharSequence[] { this.style.getInitialOffset() }, 0);
	}
}
