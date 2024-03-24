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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.runeduniverse.lib.utils.chain.errors.ChainLayerCallException;

public class BaseChainLayer implements ILayer {

	private final Method method;
	private final Class<?>[] paramTypes;
	private final Class<?> returnType;
	private final ChainLogger logger;
	private final String infoClassName;
	private final String infoMethodName;

	public BaseChainLayer(Method method, ChainLogger logger) {
		this.method = method;
		this.paramTypes = this.method.getParameterTypes();
		this.returnType = this.method.getReturnType();
		this.logger = logger;
		this.infoClassName = this.method.getDeclaringClass()
				.getSimpleName();
		this.infoMethodName = this.method.getName();
	}

	@Override
	@SuppressWarnings("deprecation")
	public void call(ChainRuntime<?> runtime) throws ChainLayerCallException {
		Object[] params = null;
		try {
			params = runtime.getParameters(this.paramTypes);
			this.logger.entering(this.infoClassName, this.infoMethodName, params);
			runtime.getTrace()
					.methodEntry(this.infoClassName, this.infoMethodName);
			runtime.storeData(this.returnType, this.method.invoke(null, params));
		} catch (Exception e) {
			throw this.packageException(this.logger.throwing(BaseChainLayer.class, "call(ChainRuntime<?>)", e), params);
		}
	}

	public ChainLayer asChainLayer(Chain chain) {
		return new ChainLayer(this, chain);
	}

	private ChainLayerCallException packageException(Exception e, Object[] passedParams) {
		StringBuilder msg = new StringBuilder(this.method.getDeclaringClass()
				.getName());
		msg.append(": " + this.returnType.getSimpleName() + ' ' + this.method.getName());
		List<String> params = new ArrayList<>();
		for (int i = 0; i < paramTypes.length; i++)
			params.add(paramTypes[i].getName() + " » " + (passedParams[i] == null ? "null"
					: passedParams[i].getClass()
							.getName()));
		msg.append(" (\n" + String.join(", \n", params) + ")");
		return new ChainLayerCallException(msg.toString(), e);
	}
}
