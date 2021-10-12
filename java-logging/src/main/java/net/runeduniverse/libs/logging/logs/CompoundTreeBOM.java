package net.runeduniverse.libs.logging.logs;

public interface CompoundTreeBOM {

	public static CharSequence INITIAL_OFFSET = "  ";
	public static CharSequence NO_ELEMENT_OFFSET = "   ";
	public static CharSequence EMPTY_ELEMENT_OFFSET = " │ ";
	public static CharSequence ELEMENT_OFFSET = " ├ ";
	public static CharSequence LAST_ELEMENT_OFFSET = " └ ";

	public static CharSequence TAGGED_TXT_BOX = "[%s]";
	public static CharSequence TAGGED_TXT_SPLITTER = " » ";

	public static boolean TAGS_TO_UPPER = true;
}
