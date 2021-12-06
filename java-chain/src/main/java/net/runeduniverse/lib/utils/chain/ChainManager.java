package net.runeduniverse.lib.utils.chain;

import static net.runeduniverse.lib.utils.common.StringUtils.isBlank;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public final class ChainManager {

	private final Set<Method> existingMethods = new HashSet<>();
	private final Map<String, ChainContainer> chains = new HashMap<>();
	private final ChainLogger logger;

	public ChainManager(Logger logger) {
		this.logger = new ChainLogger(logger);
	}

	public void addChainLayers(Class<?> carrierClass) {
		for (Method method : carrierClass.getMethods()) {
			if (this.existingMethods.contains(method))
				continue;
			this.existingMethods.add(method);
			int mods = method.getModifiers();
			if (Modifier.isAbstract(mods) || !Modifier.isStatic(mods) || !Modifier.isPublic(mods))
				continue;
			BaseChainLayer layer = new BaseChainLayer(method, this.logger);
			for (Chain anno : method.getAnnotationsByType(Chain.class)) {
				if (isBlank(anno.label()) || anno.layers() == null)
					continue;
				_getChain(anno.label()).putAtLayers(anno.layers(), layer.asChainLayer(anno));
			}
		}
	}

	public void addChainLayersOfChain(Class<?> carrierClass, String chainLabel) {
		for (Method method : carrierClass.getMethods()) {
			if (this.existingMethods.contains(method))
				continue;
			this.existingMethods.add(method);
			int mods = method.getModifiers();
			if (Modifier.isAbstract(mods) || !Modifier.isStatic(mods) || !Modifier.isPublic(mods))
				continue;
			BaseChainLayer layer = new BaseChainLayer(method, this.logger);
			for (Chain anno : method.getAnnotationsByType(Chain.class)) {
				if (isBlank(anno.label()) || anno.label() != chainLabel || anno.layers() == null)
					continue;
				_getChain(anno.label()).putAtLayers(anno.layers(), layer.asChainLayer(anno));
			}
		}
	}

	public <R> R callChain(String label, Class<R> resultType, Object... args) throws Exception {
		ChainContainer container = this.chains.get(label);
		if (container == null)
			return null;
		return container.callChain(resultType, args);
	}

	protected <R> R callChain(String label, Class<R> resultType, ChainRuntime<?> rootRuntime,
			Map<Class<?>, Object> sourceDataMap, Object... args) throws Exception {
		ChainContainer container = this.chains.get(label);
		if (container == null)
			return null;
		return container.callChain(resultType, rootRuntime, sourceDataMap, args);
	}

	private ChainContainer _getChain(String label) {
		ChainContainer c = this.chains.get(label);
		if (c != null)
			return c;
		c = new ChainContainer(this, this.logger, label);
		this.chains.put(label, c);
		return c;
	}
}
