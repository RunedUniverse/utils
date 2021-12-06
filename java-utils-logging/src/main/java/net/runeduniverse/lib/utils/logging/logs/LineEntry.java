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
