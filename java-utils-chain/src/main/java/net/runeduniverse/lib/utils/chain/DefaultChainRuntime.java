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
package net.runeduniverse.lib.utils.chain;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.runeduniverse.lib.utils.chain.api.ChainRuntime;
import net.runeduniverse.lib.utils.chain.api.Layer;
import net.runeduniverse.lib.utils.errors.ExceptionSuppressions;

@SuppressWarnings("deprecation")
public class DefaultChainRuntime<R> implements ChainRuntime<R> {

	// runtime
	@Getter
	protected final ChainRuntime<?> root;
	@Getter(onMethod_ = { @Deprecated })
	protected final DefaultChainRuntimeExecutionTrace trace;
	protected final ChainContainer container;
	protected final Store store;
	protected final ChainLogger logger;
	@Getter
	@Setter
	protected boolean canceled = false;
	@Getter
	protected boolean interrupted = false;
	// result
	@Getter
	private final Class<R> resultType;
	@Getter
	private R result;
	// execution
	private final Iterator iterator = new Iterator(Integer.MIN_VALUE);

	protected DefaultChainRuntime(final ChainContainer container, final ChainLogger logger, final Class<R> resultType,
			final Object[] args) {
		this(null, container, logger, resultType, null, args);
	}

	protected DefaultChainRuntime(final DefaultChainRuntime<?> root, final ChainContainer container,
			final ChainLogger logger, final Class<R> resultType, final Map<Class<?>, Object> sourceDataMap,
			final Object[] args) {
		this.root = root;
		this.container = container;
		this.trace = new DefaultChainRuntimeExecutionTrace(root, this.container.getLabel());
		this.logger = logger;
		this.store = new Store(this, sourceDataMap, args);
		this.resultType = resultType;
	}

	@Override
	public <S> S callSubChain(String label, Class<S> resultType, Object... args) throws Exception {
		return container.getManager()
				.callChain(label, resultType, this, null, args);
	}

	@Override
	public <S> S callSubChainWithSourceData(final String label, final Class<S> resultType, final Object... args)
			throws Exception {
		return container.getManager()
				.callChain(label, resultType, this, this.store.getSourceDataMap(), args);
	}

	@Override
	public <S> S callSubChainWithRuntimeData(final String label, final Class<S> resultType, final Object... args)
			throws Exception {
		return container.getManager()
				.callChain(label, resultType, this, this.store.getRuntimeDataMap(), args);
	}

	@Override
	public void executeOnChain(final Map<Integer, Layer> chain, final int lowestId, final int highestId)
			throws ExceptionSuppressions {
		Set<Exception> errors = new HashSet<>();
		boolean noErrors = true;

		for (this.iterator.setI(lowestId); !this.interrupted && this.iterator.getI() <= highestId; this.iterator
				.next()) {
			Layer layer = chain.get(this.iterator.getI());
			if (layer != null)
				try {
					if ((noErrors || ChainLayer.ignoreErrors(layer))
							&& (this.active() || ChainLayer.ignoreInActive(layer))) {
						this.trace.setCurrentLayer(this.iterator.getI());
						layer.call(this);
					}
				} catch (Exception e) {
					errors.add(e);
					noErrors = false;
				}
		}

		this.trace.report(this.logger);

		if (!noErrors)
			throw this.logger.throwing(DefaultChainRuntime.class,
					"executeOnChain(final Map<Integer, ILayer>, int, int)",
					new ExceptionSuppressions("ChainRuntime[" + this.hashCode() + "] of Chain<"
							+ this.container.getLabel() + "> errored out!", true).addSuppressed(errors));
	}

	@Override
	public void jumpToLayer(final int layerId) {
		this.iterator.setI(layerId);
		this.trace.jumpToLayer(layerId);
	}

	@Override
	public void interrupt() {
		this.interrupted = true;
	}

	@Override
	public <D extends Object> D storeData(final D entity) {
		return this.storeData(entity.getClass(), entity);
	}

	@Override
	public <D extends Object> D storeData(final Class<?> type, final D entity) {
		if (entity instanceof DefaultChainRuntime<?> || entity instanceof Store)
			return entity;

		this.store.putData(type, entity);
		return entity;
	}

	@Override
	public void setResult(final R result) {
		this.result = result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean setPossibleResult(final Object entity) {
		if (this.resultType == null || entity == null)
			return false;
		if (this.resultType.isAssignableFrom(entity.getClass())) {
			this.result = (R) entity;
			return true;
		}
		return false;
	}

	@Override
	public Object[] getParameters(final Class<?>[] paramTypes) {
		return this.store.getData(paramTypes);
	}

	@Override
	public boolean active() {
		if (this.canceled || this.result != null)
			return false;
		return true;
	}

	@Override
	public boolean isRoot() {
		return this.root == null;
	}

	@Override
	public boolean hasResult() {
		return this.result != null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public R getFinalResult() {
		if (this.canceled)
			return null;

		if (this.resultType == null)
			return (R) store.getLastAdded();
		else if (this.result != null)
			return this.result;
		return store.getData(this.resultType);
	}

	@Getter
	@Setter
	@AllArgsConstructor
	private static class Iterator {
		private int i;

		public void next() {
			this.i = this.i + 1;
		}
	}
}
