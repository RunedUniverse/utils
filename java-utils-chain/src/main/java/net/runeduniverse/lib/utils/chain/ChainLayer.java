/*
 * Copyright © 2024 VenaNocta (venanocta@gmail.com)
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
package net.runeduniverse.lib.utils.chain;

public final class ChainLayer implements ILayer {

	private final BaseChainLayer base;
	private final boolean ignoreCancelled;
	private final boolean ignoreResult;
	private final boolean ignoreErrors;

	protected ChainLayer(BaseChainLayer baseLayer, Chain chain) {
		this.base = baseLayer;
		this.ignoreCancelled = chain.ignoreCancelled();
		this.ignoreResult = chain.ignoreResult();
		this.ignoreErrors = chain.ignoreErrors();
	}

	@Override
	public void call(ChainRuntime<?> runtime) throws Exception {
		this.base.call(runtime);
	}

	public boolean ignoreCancelled() {
		return this.ignoreCancelled;
	}

	public static boolean ignoreCancelled(ILayer layer) {
		return layer instanceof ChainLayer && ((ChainLayer) layer).ignoreCancelled();
	}

	public boolean ignoreResult() {
		return this.ignoreResult;
	}

	public static boolean ignoreResult(ILayer layer) {
		return layer instanceof ChainLayer && ((ChainLayer) layer).ignoreResult();
	}

	public boolean ignoreErrors() {
		return this.ignoreErrors;
	}

	public static boolean ignoreErrors(ILayer layer) {
		return layer instanceof ChainLayer && ((ChainLayer) layer).ignoreErrors();
	}

	public boolean ignoreInActive() {
		return this.ignoreResult || this.ignoreCancelled;
	}

	public static boolean ignoreInActive(ILayer layer) {
		return layer instanceof ChainLayer && ((ChainLayer) layer).ignoreInActive();
	}
}
