package net.runeduniverse.libs.chain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import net.runeduniverse.libs.logging.DebugLogger;

import net.runeduniverse.libs.chain.model.Player;

public class ChainManagerTest {

	public static final String TEST_CHAIN_LABEL = "SYSTEM_TEST";
	public static final String COUNT_TEST_CHAIN_LABEL = "SYSTEM_TEST_COUNT";
	public static final String PRINT_A_TEST_CHAIN_LABEL = "SYSTEM_TEST_PRINT_A";
	public static final String PRINT_B_TEST_CHAIN_LABEL = "SYSTEM_TEST_PRINT_B";
	public static final String CHECK_RUNTIME_LABEL = "SYSTEM_TEST_PRINT_B";

	private ChainManager manager = new ChainManager(new DebugLogger());

	public ChainManagerTest() {
		this.manager.addChainLayers(ChainManagerTest.class);
	}

	@Test
	@Tag("system")
	public void test() throws Exception {
		Class<?> type = Player.class;
		this.manager.callChain(TEST_CHAIN_LABEL, type, type);
	}

	@Test
	@Tag("system")
	public void count() throws Exception {
		this.manager.callChain(COUNT_TEST_CHAIN_LABEL, Void.class);
	}

	@Test
	@Tag("system")
	public void print() throws Exception {
		this.manager.callChain(PRINT_A_TEST_CHAIN_LABEL, Void.class, "A");
		this.manager.callChain(PRINT_B_TEST_CHAIN_LABEL, Void.class, "B");
	}

	@Test
	@Tag("system")
	public void checkRuntime() throws Exception {
		this.manager.callChain(CHECK_RUNTIME_LABEL, Void.class);
	}

	@Chain(label = TEST_CHAIN_LABEL, layers = { 100 })
	public static <T> T test(Class<T> type) throws InstantiationException, IllegalAccessException {
		System.out.println(type.getCanonicalName());
		return type.newInstance();
	}

	@Chain(label = COUNT_TEST_CHAIN_LABEL, layers = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 })
	public static Number printCount(Integer number) {
		if (number == null)
			number = 0;
		System.out.println(number);
		return number + 1;
	}

	@Chain(label = PRINT_A_TEST_CHAIN_LABEL, layers = { 200 })
	@Chain(label = PRINT_B_TEST_CHAIN_LABEL, layers = { 400 })
	public static void printString(String str) {
		System.out.println(str);
	}

	@Chain(label = CHECK_RUNTIME_LABEL, layers = { 0 })
	public static void checkRuntimeEntity(final ChainRuntime<?> runtime) {
		assertNotNull(runtime, "ChainRuntime<?> did not get passed to layer");
	}
}
