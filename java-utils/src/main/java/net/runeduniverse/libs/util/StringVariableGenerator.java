package net.runeduniverse.libs.util;

public class StringVariableGenerator {

	String var = "`";

	public String nextVal() {
		this.var = inc(var.length() - 1);
		return this.var;
	}

	private String inc(int i) {
		char last = this.var.charAt(i);
		if (last == 'z')
			return (i == 0 ? "a" : inc(i - 1)) + 'a';
		else
			return this.var.substring(0, i) + (char) (last + 1);
	}

}
