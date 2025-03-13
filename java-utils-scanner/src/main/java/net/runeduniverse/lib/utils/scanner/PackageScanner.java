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
package net.runeduniverse.lib.utils.scanner;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;

import lombok.NoArgsConstructor;
import net.runeduniverse.lib.utils.common.LinkedDataHashMap;
import net.runeduniverse.lib.utils.common.api.DataMap;
import net.runeduniverse.lib.utils.scanner.api.TypeScanner;
import net.runeduniverse.lib.utils.scanner.debug.DefaultIntercepter;
import net.runeduniverse.lib.utils.scanner.debug.api.Intercepter;

@NoArgsConstructor
public class PackageScanner {

	private final Set<ClassLoader> loader = new HashSet<>();
	private final Set<String> pkgs = new HashSet<>();
	private final Set<TypeScanner> scanner = new HashSet<>();
	private final Set<Exception> errors = new HashSet<>();
	private boolean includeSubPkgs = false;
	private boolean debug = false;
	private Validator validator = null;

	public PackageScanner includeClassLoader(List<ClassLoader> loader) {
		this.loader.addAll(loader);
		return this;
	}

	public PackageScanner includeClassLoader(ClassLoader... loader) {
		this.loader.addAll(Arrays.asList(loader));
		return this;
	}

	public PackageScanner includePackages(List<String> pkgs) {
		this.pkgs.addAll(pkgs);
		return this;
	}

	public PackageScanner includePackages(String... pkgs) {
		this.pkgs.addAll(Arrays.asList(pkgs));
		return this;
	}

	public <SCANNER extends TypeScanner> PackageScanner includeScanner(List<SCANNER> scanner) {
		this.scanner.addAll(scanner);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <SCANNER extends TypeScanner> PackageScanner includeScanner(SCANNER... scanner) {
		this.scanner.addAll(Arrays.asList(scanner));
		return this;
	}

	public PackageScanner includeValidator(Validator validator) {
		this.validator = validator;
		return this;
	}

	public PackageScanner includeOptions(Object... options) {
		for (Object obj : options) {
			if (obj instanceof Collection<?>)
				for (Object element : (Collection<?>) obj)
					this.includeOptions(element);
			if (obj instanceof ClassLoader)
				this.loader.add((ClassLoader) obj);
			if (obj instanceof TypeScanner)
				this.scanner.add((TypeScanner) obj);
			if (obj instanceof String)
				this.pkgs.add((String) obj);
			if (obj instanceof Validator)
				this.validator = (Validator) obj;
		}
		return this;
	}

	public PackageScanner includeSubPkgs() {
		this.includeSubPkgs = true;
		return this;
	}

	public PackageScanner enableDebugMode(boolean active) {
		this.debug = active;
		return this;
	}

	public PackageScanner scan() {
		if (this.loader.isEmpty())
			this.loader.add(PackageScanner.class.getClassLoader());

		DataMap<Class<?>, ClassLoader, String> classes = new LinkedDataHashMap<>();
		DefaultIntercepter i = new DefaultIntercepter("PackageScanner INFO", this.debug);
		Intercepter iPkg = i.addSection("URL", "Discovered URL's");
		Intercepter iClass = i.addSection("CLASS", "Classes");
		Intercepter iLoader = i.addSection("LOADER", "ClassLoader");
		synchronized (this.loader) {
			for (ClassLoader classLoader : this.loader) {
				iLoader.intercept(classLoader);
				for (String pkg : this.pkgs)
					loadResources(classes, classLoader, pkg, iPkg, iClass);
			}
		}
		i.print();

		synchronized (this.scanner) {
			classes.forEach((c, l, p) -> {
				for (TypeScanner s : PackageScanner.this.scanner)
					try {
						s.scan(c, l, p);
					} catch (Exception e) {
						this.errors.add(e);
					}
			});
		}

		if (this.validator != null)
			try {
				this.validator.validate();
			} catch (Exception e) {
				this.errors.add(e);
			}
		return this;
	}

	public <E extends Exception> void throwSurpressions(E exception) throws E {
		if (errors.isEmpty())
			return;
		synchronized (this.errors) {
			errors.forEach(e -> exception.addSuppressed(e));
			this.errors.clear();
		}
		throw exception;
	}

	public void throwSurpressions() throws Exception {
		this.throwSurpressions(new Exception("Package scanning failed! See surpressed Exceptions!"));
	}

	/**
	 * Scans all classes accessible from the context class loader which belong to
	 * the given package and subpackages.
	 */
	private void loadResources(DataMap<Class<?>, ClassLoader, String> classes, ClassLoader classLoader, String pkg,
			Intercepter iPkg, Intercepter iClass) {
		Enumeration<URL> resources;
		String zpkg = pkg.replace('.', '/') + '/';
		try {
			resources = classLoader.getResources(zpkg);
		} catch (IOException e) {
			return;
		}

		Map<String, URL> pkgEntries = new HashMap<>();
		URL url = null;
		URLConnection c = null;
		Enumeration<JarEntry> entries = null;
		while (resources.hasMoreElements()) {
			url = iPkg.intercept(resources.nextElement());
			try {
				c = url.openConnection();
			} catch (IOException e) {
				c = null;
				continue;
			}
			if (!(c instanceof JarURLConnection)) {
				// local path detected:
				findClasses(classes, classLoader, new File(url.getFile()), pkg, iClass);
				continue;
			}
			try {
				entries = ((JarURLConnection) c).getJarFile()
						.entries();
			} catch (IOException e) {
				entries = null;
				continue;
			}

			while (entries.hasMoreElements()) {
				String entryName = entries.nextElement()
						.getName();
				if (!(entryName.endsWith(".class") && entryName.startsWith(zpkg)))
					continue;
				entryName = entryName.substring(zpkg.length(), entryName.length() - 6);
				if (entryName.indexOf('/') != -1) {
					if (this.includeSubPkgs)
						pkgEntries.put(entryName.replace('/', '.'), url);
				} else
					pkgEntries.put(entryName, url);
			}
		}

		URLClassLoader urlLoader = classLoader instanceof URLClassLoader ? (URLClassLoader) classLoader
				: new URLClassLoader((URL[]) new HashSet<>(pkgEntries.values()).toArray(), classLoader);

		for (String clazzName : pkgEntries.keySet()) {
			try {
				classes.put(Class.forName(iClass.intercept(pkg + '.' + clazzName), true, urlLoader), classLoader, pkg);
			} catch (ClassNotFoundException e) {
			}
		}
	}

	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 */
	private void findClasses(DataMap<Class<?>, ClassLoader, String> classes, ClassLoader classLoader, File directory,
			String pkg, Intercepter i) {
		if (!directory.exists())
			return;
		for (File file : directory.listFiles()) {
			if (file.isDirectory() && !file.getName()
					.contains(".")) {

				if (this.includeSubPkgs)
					findClasses(classes, classLoader, file, pkg + "." + file.getName(), i);
			} else if (file.getName()
					.endsWith(".class"))
				try {
					classes.put(Class.forName(i.intercept(pkg + '.' + file.getName()
							.substring(0, file.getName()
									.length() - 6)),
							true, classLoader), classLoader, pkg);
				} catch (ClassNotFoundException e) {
				}
		}
	}

	public static interface Validator {

		public void validate() throws Exception;

	}

}
