/*
 * Copyright © 2022 Pl4yingNight (pl4yingnight@gmail.com)
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

import net.runeduniverse.lib.utils.logging.logs.CompoundTree;

public class ChainRuntimeExecutionTrace {

	public static String HEAD_TXT = "CHAIN EXECUTION TRACE » %s%n%s";
	public static String METHOD_ENTRY_TXT = "[%d] ENTRY » %s.%s";
	public static String JUMP_TXT = "[%d] JUMP  » [%d]";

	private final boolean hasParent;
	private final CompoundTree tree;
	private final String chainLabel;
	private boolean dirty = true;
	private String report;

	private int activeExecutionLevel = -1;

	@SuppressWarnings("deprecation")
	public ChainRuntimeExecutionTrace(ChainRuntime<?> runtime, String chainLabel) {
		this.tree = new CompoundTree("CHAIN", this.chainLabel = chainLabel);
		if (runtime == null)
			this.hasParent = false;
		else {
			this.hasParent = true;
			runtime.getTrace()
					.append(this.tree);
		}
	}

	private void append(CompoundTree tree) {
		this.dirty = true;
		this.tree.append(tree);
	}

	public void setCurrentLayer(int i) {
		this.dirty = true;
		this.activeExecutionLevel = i;
	}

	public void methodEntry(String className, String methodName) {
		this.dirty = true;
		this.tree.append(String.format(METHOD_ENTRY_TXT, this.activeExecutionLevel, className, methodName));
	}

	public void jumpToLayer(int jumpLayer) {
		this.dirty = true;
		this.tree.append(String.format(JUMP_TXT, this.activeExecutionLevel, jumpLayer));
	}

	private String buildReport() {
		if (this.dirty == false)
			return this.report;
		this.report = tree.toString();
		this.dirty = false;
		return this.report;
	}

	@Override
	public String toString() {
		return String.format(HEAD_TXT, this.chainLabel, this.buildReport());
	}

	public String report(ChainLogger logger) {
		if (!this.hasParent)
			logger.logTrace(this);
		return this.report;
	}
}
