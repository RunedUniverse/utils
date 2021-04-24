package net.runeduniverse.libs.scanner.debug;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public class StringListBuilder implements java.io.Serializable, Appendable, CharSequence {

	public static final String DEFAULT_INDENT = "   ";
	public static final String DEFAULT_HEADLINE = "LIST";

	private static final long serialVersionUID = 7051551890872524506L;

	private final StringBuilder builder = new StringBuilder();
	private final CharSequence lineIndent;

	public StringListBuilder() {
		this(StringListBuilder.DEFAULT_HEADLINE, StringListBuilder.DEFAULT_INDENT);
	}

	public StringListBuilder(CharSequence headline) {
		this(headline, StringListBuilder.DEFAULT_INDENT);
	}

	public StringListBuilder(CharSequence headline, CharSequence lineIndent) {
		this.builder.append(DEFAULT_HEADLINE);
		this.builder.append(':');
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
		return this.builder.toString();
	}
}