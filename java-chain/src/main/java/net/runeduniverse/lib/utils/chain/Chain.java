package net.runeduniverse.lib.utils.chain;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
@Repeatable(Chains.class)
public @interface Chain {
	String label();

	int[] layers();

	boolean ignoreCancelled() default false;

	boolean ignoreResult() default false;

	boolean ignoreErrors() default false;
}
