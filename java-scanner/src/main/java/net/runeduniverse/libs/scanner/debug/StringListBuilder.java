package net.runeduniverse.libs.scanner.debug;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import lombok.Setter;

public class StringListBuilder implements java.io.Serializable, Appendable, CharSequence {

	public static final CharSequence DEFAULT_INDENT = "   ";

	private static final long serialVersionUID = 7051551890872524506L;

	private final StringBuilder builder = new StringBuilder();
	@Setter
	private CharSequence headline = "LIST";
	private final CharSequence lineIndent;

	public StringListBuilder() {
		this.lineIndent = StringListBuilder.DEFAULT_INDENT;
	}

	public StringListBuilder(CharSequence headline) {
		this(headline, StringListBuilder.DEFAULT_INDENT);
	}

	public StringListBuilder(CharSequence headline, CharSequence lineIndent) {
		this.headline = headline;
		this.lineIndent = lineIndent;
	}

	@Override
	public int length() {
		return this.builder.length();
	}

	@Override
	public char charAt(int index) {
		return this.builder.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return this.builder.subSequence(start, end);
	}

	@Override
	public Appendable append(CharSequence csq) throws IOException {
		return this.builder.append(csq);
	}

	@Override
	public Appendable append(CharSequence csq, int start, int end) throws IOException {
		return this.builder.append(csq, start, end);
	}

	@Override
	public Appendable append(char c) throws IOException {
		return this.builder.append(c);
	}

	public StringListBuilder appendElement(CharSequence csq) {
		this.builder.append('\n');
		this.builder.append(this.lineIndent);
		this.builder.append(csq);
		return this;
	}

	public StringListBuilder appendElements(Collection<CharSequence> csqcol) {
		for (CharSequence csq : csqcol)
			this.appendElement(csq);
		return this;
	}

	public StringListBuilder appendElements(CharSequence... csq) {
		return this.appendElements(Arrays.asList(csq));
	}

	public StringListBuilder append(StringListBuilder builder) {
		return this.appendElement(builder.builder);
	}

	@Override
	public String toString() {
		return headline + ":" + this.builder.toString();
	}
}