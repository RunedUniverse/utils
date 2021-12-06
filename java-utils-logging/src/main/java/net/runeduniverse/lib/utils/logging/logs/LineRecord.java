package net.runeduniverse.lib.utils.logging.logs;

import static net.runeduniverse.lib.utils.logging.logs.CompoundTreeBOM.*;

import lombok.Getter;

public class LineRecord {

	protected CharSequence tag = null;
	protected CharSequence content = null;
	@Getter
	private int tagSize;

	public LineRecord(CharSequence content) {
		this(null, content);
	}

	public LineRecord(CharSequence tag, CharSequence content) {
		if (tag == null)
			this.tagSize = 0;
		else {
			this.tag = TAGS_TO_UPPER ? tag.toString()
					.toUpperCase() : tag.toString();
			this.tagSize = this.tag.length();
		}
		this.content = content;
	}

	protected String buildLine(int tagSize) {
		if (this.tag == null)
			return (String) this.content + '\n';

		StringBuilder builder = new StringBuilder(String.format(TAGGED_TXT_BOX.toString(), this.tag));
		for (int i = 0; i < (tagSize - this.tagSize); i++)
			builder.append(' ');
		builder.append(TAGGED_TXT_SPLITTER)
				.append(this.content)
				.append('\n');
		return builder.toString();
	}

	public String write(CharSequence[] baseOffset, int tagSize) {
		return String.join("", baseOffset) + this.buildLine(tagSize);
	}
}
