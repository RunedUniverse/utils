/*
 * Copyright © 2025 VenaNocta (venanocta@gmail.com)
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
package net.runeduniverse.lib.utils.conditional.tools;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.runeduniverse.lib.utils.common.StringVariableGenerator;
import net.runeduniverse.lib.utils.conditional.api.Condition;
import net.runeduniverse.lib.utils.conditional.api.ConditionGroup;
import net.runeduniverse.lib.utils.conditional.api.ConditionModifier;
import net.runeduniverse.lib.utils.logging.log.DefaultCompoundTree;
import net.runeduniverse.lib.utils.logging.log.api.CompoundTree;

import static net.runeduniverse.lib.utils.common.StringUtils.isBlank;

public class ConditionIndexer {

	public CompoundTree toRecord(final Condition<?> condition) {
		final StringVariableGenerator gen = new StringVariableGenerator();
		final CompoundTree tree = new DefaultCompoundTree("Condition Tree");
		toRecord(gen, new LinkedHashMap<>(), tree, condition);
		return tree;
	}

	protected void toRecord(final StringVariableGenerator gen, final Map<Condition<?>, CharSequence> parentStack,
			final CompoundTree parentTree, final Condition<?> condition) {
		if (condition == null)
			return;
		CharSequence num = parentStack.get(condition);
		if (num != null) {
			parentTree.append("!" + num, condition.getType());
			return;
		}

		final Map<Condition<?>, CharSequence> stack = new LinkedHashMap<>();
		stack.putAll(parentStack);
		stack.put(condition, num = gen.nextVal());
		final CompoundTree tree = new DefaultCompoundTree("#" + num, condition.getType());
		parentTree.append(tree);

		tree.append("priority", Integer.toString(condition.getPriority()));

		final List<Condition.ConditionInfo> infos = condition.getInfo();
		if (infos != null && !infos.isEmpty()) {
			for (Condition.ConditionInfo info : infos)
				tree.append(info.getTag(), info.getText());
		}

		if (condition instanceof ConditionGroup) {
			for (Condition<?> con : ((ConditionGroup<?>) condition).getGroup())
				toRecord(gen, stack, tree, con);
		} else if (condition instanceof ConditionModifier) {
			toRecord(gen, stack, tree, ((ConditionModifier<?>) condition).getCondition());
		}
	}

	public CompoundTree detectCircle(final Condition<?> condition) {
		return detectCircle(new StringVariableGenerator(), new LinkedHashMap<>(), condition);
	}

	protected CompoundTree detectCircle(final StringVariableGenerator gen,
			final Map<Condition<?>, CharSequence> parentStack, final Condition<?> condition) {
		if (condition == null)
			return null;
		CharSequence num = parentStack.get(condition);
		if (num != null)
			return createTraceTree(num, condition, null);

		final Map<Condition<?>, CharSequence> stack = new LinkedHashMap<>();
		stack.putAll(parentStack);
		stack.put(condition, num = gen.nextVal());
		CompoundTree addTree = null;

		if (condition instanceof ConditionGroup) {
			for (Condition<?> con : ((ConditionGroup<?>) condition).getGroup()) {
				addTree = detectCircle(gen, stack, con);
				if (addTree != null)
					return createTraceTree(num, condition, addTree);
			}
		} else if (condition instanceof ConditionModifier) {
			addTree = detectCircle(gen, stack, ((ConditionModifier<?>) condition).getCondition());
			if (addTree != null)
				return createTraceTree(num, condition, addTree);
		}
		return null;
	}

	protected CompoundTree createTraceTree(final CharSequence num, final Condition<?> condition,
			final CompoundTree addition) {
		String txt = condition.getType();
		if (isBlank(txt))
			txt = Condition.TXT_UNKNOWN_TYPE;
		final CompoundTree tree = new DefaultCompoundTree("#" + num, txt + '@' + condition.getClass()
				.getCanonicalName());
		if (addition != null)
			tree.append(addition);
		return tree;
	}
}
