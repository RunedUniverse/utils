package net.runeduniverse.libs.logging.logs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static net.runeduniverse.libs.logging.logs.CompoundTreeBOM.*;

public class TreeRecord extends LineRecord {
	private List<LineRecord> subRecords = new ArrayList<>();
	private int mxTagSize = 0;

	public TreeRecord(CharSequence title) {
		super(title);
	}

	public TreeRecord(CharSequence tag, CharSequence title) {
		super(tag, title);
	}

	public TreeRecord append(LineRecord record) {
		this.subRecords.add(record);
		int s;
		if ((s = record.getTagSize()) > this.mxTagSize)
			this.mxTagSize = s;
		return this;
	}

	@Override
	public String write(CharSequence[] baseOffset, int tagSize) {
		StringBuilder builder = new StringBuilder(String.join("", baseOffset)).append(this.buildLine(tagSize));
		CharSequence[] offset = new CharSequence[baseOffset.length + 1];
		for (int i = 0; i < baseOffset.length; i++)
			offset[i] = baseOffset[i];
		if (baseOffset.length > 1) {
			if (baseOffset[baseOffset.length - 1] == ELEMENT_OFFSET)
				offset[baseOffset.length - 1] = EMPTY_ELEMENT_OFFSET;
			if (baseOffset[baseOffset.length - 1] == LAST_ELEMENT_OFFSET)
				offset[baseOffset.length - 1] = NO_ELEMENT_OFFSET;
		}

		for (Iterator<LineRecord> iterator = subRecords.iterator(); iterator.hasNext();) {
			LineRecord r = (LineRecord) iterator.next();
			if (iterator.hasNext())
				offset[baseOffset.length] = ELEMENT_OFFSET;
			else
				offset[baseOffset.length] = LAST_ELEMENT_OFFSET;
			builder.append(r.write(offset, this.mxTagSize));
		}
		return builder.toString();
	}

	@Override
	public String toString() {
		return this.write(new CharSequence[] { INITIAL_OFFSET }, 0);
	}
}
