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
package net.runeduniverse.lib.utils.chain.api;

import java.util.Map;

public interface ChainRuntime<R> {

	public ChainRuntime<?> getRoot();

	@Deprecated
	public ChainRuntimeExecutionTrace getTrace();

	public boolean active();

	public boolean isCanceled();

	public boolean isInterrupted();

	public boolean isRoot();

	public Object[] getParameters(Class<?>[] paramTypes);

	public Class<R> getResultType();

	public boolean hasResult();

	public R getResult();

	public R getFinalResult();

	public void setCanceled(boolean canceled);

	public void setResult(R result);

	public boolean setPossibleResult(Object entity);

	public <D extends Object> D storeData(D entity);

	public <D extends Object> D storeData(Class<?> type, D entity);

	public void interrupt();

	public <S> S callSubChain(String label, Class<S> resultType, Object... args) throws Exception;

	public <S> S callSubChainWithSourceData(String label, Class<S> resultType, Object... args) throws Exception;

	public <S> S callSubChainWithRuntimeData(String label, Class<S> resultType, Object... args) throws Exception;

	/**
	 * <b>Internal Method!</b> Executes this runtime on a given chain.
	 *
	 * @param chain     Map of Layers used in the chain
	 * @param lowestId  lowest LayerId to be executed
	 * @param highestId highest LayerId to be executed
	 * @throws Exception throws a tracing Exception including all underlying
	 *                   Exceptions as surpressed Exceptions
	 */
	public void executeOnChain(final Map<Integer, Layer> chain, final int lowestId, final int highestId)
			throws Exception;

	public void jumpToLayer(int layerId);

}
