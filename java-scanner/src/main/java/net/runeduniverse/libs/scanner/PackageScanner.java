package net.runeduniverse.libs.scanner;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.NoArgsConstructor;
import net.runeduniverse.libs.scanner.debug.Intercepter;
import net.runeduniverse.libs.utils.DataHashMap;
import net.runeduniverse.libs.utils.DataMap;

@NoArgsConstructor
public class PackageScanner {

	private final Set<ClassLoader> loader = new HashSet<>();
	private final Set<String> pkgs = new HashSet<>();
	private final Set<ITypeScanner> scanner = new HashSet<>();
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

	public <SCANNER extends ITypeScanner> PackageScanner includeScanner(List<SCANNER> scanner) {
		this.scanner.addAll(scanner);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <SCANNER extends ITypeScanner> PackageScanner includeScanner(SCANNER... scanner) {
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
			if (obj instanceof ITypeScanner)
				this.scanner.add((ITypeScanner) obj);
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

		DataMap<Class<?>, ClassLoader, String> classes = new DataHashMap<>();
		Intercepter i = new Intercepter("PackageScanner INFO", this.debug);
		Intercepter iPkg = i.addSection("Discovered URL's");
		Intercepter iClass = iPkg.addSection("Classes");
		for (ClassLoader classLoader : loader)
			for (String pkg : pkgs)
				findClasses(classes, classLoader, pkg, iPkg, iClass);

		classes.forEach((c, l, p) -> {
			for (ITypeScanner s : PackageScanner.this.scanner)
				try {
					s.scan(c, l, p);
				} catch (Exception e) {
					this.errors.add(e);
				}
		});
		i.print();

		if (this.validator != null)
			try {
				this.validator.validate();
			} catch (Exception e) {
				this.errors.add(e);
			}
		return this;
	}

	public void throwSurpressions(Exception exception) throws Exception {
		if (errors.isEmpty())
			return;
		errors.forEach(e -> exception.addSuppressed(e));
		throw exception;
	}

	public void throwSurpressions() throws Exception {
		this.throwSurpressions(new Exception("Package scanning failed! See surpressed Exceptions!"));
	}

	/**
	 * Scans all classes accessible from the context class loader which belong to
	 * the given package and subpackages.
	 */
	private void findClasses(DataMap<Class<?>, ClassLoader, String> classes, ClassLoader classLoader, String pkg,
			Intercepter iPkg, Intercepter iClass) {
		Enumeration<URL> resources;
		try {
			resources = classLoader.getResources(pkg.replace('.', '/'));
		} catch (IOException e) {
			return;
		}
		List<File> dirs = new ArrayList<>();
		while (resources.hasMoreElements())
			dirs.add(new File(iPkg.intercept(resources.nextElement())
					.getFile()));
		for (File directory : dirs)
			findClasses(classes, classLoader, directory, pkg, iClass);
	}

	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 */
	private void findClasses(DataMap<Class<?>, ClassLoader, String> classes, ClassLoader classLoader, File directory,
			String pkg, Intercepter i) {
		if (!directory.exists())
			return;
		for (File file : directory.listFiles())
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

	public static interface Validator {
		public void validate() throws Exception;
	}
}
