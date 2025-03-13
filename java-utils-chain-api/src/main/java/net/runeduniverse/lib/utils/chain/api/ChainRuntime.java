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

public interface ChainRuntime<R> {

	public ChainRuntime<?> getRoot();

	@Deprecated
	public ChainRuntimeExecutionTrace getTrace();

	public boolean isCanceled();

	public boolean isInterrupted();

	public Class<R> getResultType();

	public R getResult();

	public void setCanceled(boolean canceled);

	public void interrupt();

	public <S> S callSubChain(String label, Class<S> resultType, Object... args) throws Exception;

	public <S> S callSubChainWithSourceData(String label, Class<S> resultType, Object... args) throws Exception;

	public <S> S callSubChainWithRuntimeData(String label, Class<S> resultType, Object... args) throws Exception;

	public void jumpToLayer(int layerId);

	public <D extends Object> D storeData(D entity);

	public <D extends Object> D storeData(Class<?> type, D entity);

	public void setResult(R result);

	public boolean setPossibleResult(Object entity);

	public Object[] getParameters(Class<?>[] paramTypes);

	public boolean active();

	public boolean isRoot();

	public boolean hasResult();

}
