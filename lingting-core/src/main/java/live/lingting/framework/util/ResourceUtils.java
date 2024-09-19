package live.lingting.framework.util;

import live.lingting.framework.function.ThrowingSupplier;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static live.lingting.framework.util.ResourceUtils.Resource.PROTOCOL_FILE;
import static live.lingting.framework.util.ResourceUtils.Resource.PROTOCOL_JAR;

/**
 * @author lingting 2024-09-11 17:20
 */
@UtilityClass
public class ResourceUtils {

	public static ClassLoader currentClassLoader() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		return loader == null ? ClassLoader.getSystemClassLoader() : loader;
	}

	public static Resource get(String name) throws IOException {
		ClassLoader loader = currentClassLoader();
		return get(loader, name);
	}

	@SneakyThrows
	public static Resource get(ClassLoader loader, String name) throws IOException {
		URL resource = loader.getResource(name);
		if (resource == null) {
			return null;
		}
		String url = resource.toString();
		String protocol = StringUtils.substringBefore(url, ":/");
		if (protocol.startsWith(PROTOCOL_FILE)) {
			URI uri = resource.toURI();
			File dir = new File(uri);
			return Resource.of(protocol, dir);
		}

		if (protocol.startsWith(PROTOCOL_JAR)) {
			URLConnection connection = resource.openConnection();
			if (connection instanceof JarURLConnection jar) {
				String[] split = url.split(Resource.DELIMITER_JAR);
				int size = split.length - 1;
				Collection<String> paths = Arrays.stream(split).limit(size).toList();
				JarEntry entry = jar.getJarEntry();
				return new Resource(protocol, paths, entry.getName(), entry.isDirectory());
			}
		}
		return null;
	}

	public static Collection<Resource> scan(String name) throws IOException {
		return scan(name, resource -> true);
	}

	/**
	 * 扫描资源
	 * @param name 名称
	 * @param predicate 是否返回该资源
	 * @return 所有满足条件的资源对象
	 */
	public static Collection<Resource> scan(String name, Predicate<Resource> predicate) throws IOException {
		ClassLoader loader = currentClassLoader();
		return scan(loader, name, predicate);
	}

	public static Collection<Resource> scan(ClassLoader loader, String name, Predicate<Resource> predicate)
			throws IOException {
		Enumeration<URL> resources = loader.getResources(name);
		Collection<Resource> result = new LinkedHashSet<>();
		while (resources.hasMoreElements()) {
			URL url = resources.nextElement();
			handler(result, url, predicate);
		}
		return result;
	}

	@SneakyThrows
	static void handler(Collection<Resource> result, URL resource, Predicate<Resource> predicate) {
		String url = resource.toString();
		String protocol = StringUtils.substringBefore(url, ":/");

		if (protocol.startsWith(PROTOCOL_FILE)) {
			URI uri = resource.toURI();
			File dir = new File(uri);
			if (!dir.isDirectory()) {
				fill(result, () -> Resource.of(protocol, dir), predicate);
				return;
			}
			try (Stream<Path> walk = Files.walk(dir.toPath())) {
				walk.forEach(path -> fill(result, () -> Resource.of(protocol, path.toFile()), predicate));
			}
			return;
		}

		if (protocol.startsWith(PROTOCOL_JAR)) {
			URLConnection connection = resource.openConnection();
			if (connection instanceof JarURLConnection jar) {
				String[] split = url.split(Resource.DELIMITER_JAR);
				int size = split.length - 1;
				Collection<String> paths = Arrays.stream(split).limit(size).toList();
				JarFile file = jar.getJarFile();
				Enumeration<JarEntry> entries = file.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					fill(result, () -> new Resource(protocol, paths, entry.getName(), entry.isDirectory()), predicate);
				}
			}
		}
	}

	@SneakyThrows
	static void fill(Collection<Resource> resources, ThrowingSupplier<Resource> supplier,
			Predicate<Resource> predicate) {
		Resource resource = supplier.get();
		if (predicate.test(resource)) {
			resources.add(resource);
		}
	}

	@Getter
	public static class Resource {

		public static final String DELIMITER_JAR = "!";

		public static final String DELIMITER_FILE = "/";

		public static final String PROTOCOL_JAR = "jar";

		public static final String PROTOCOL_FILE = "file";

		protected final String protocol;

		protected final Collection<String> paths;

		protected final String name;

		protected final boolean directory;

		protected final boolean jar;

		protected final boolean file;

		protected final String delimiter;

		protected final String path;

		protected URI uri;

		protected URL url;

		public Resource(String protocol, Collection<String> paths, String name, boolean directory) {
			this.protocol = protocol;
			this.paths = Collections.unmodifiableCollection(paths);
			this.name = name;
			this.directory = directory;
			this.jar = PROTOCOL_JAR.startsWith(protocol);
			this.file = PROTOCOL_FILE.startsWith(protocol);
			this.delimiter = this.jar ? DELIMITER_JAR : DELIMITER_FILE;
			String suffix = isJar() && !name.startsWith("/") ? "/" + name : name;
			this.path = this.paths.stream().collect(Collectors.joining(DELIMITER_FILE, "", delimiter + suffix));
		}

		public static Resource of(String protocol, File file) {
			List<String> paths = Arrays
				.stream(file.getParentFile().getAbsoluteFile().toURI().toString().split(DELIMITER_FILE))
				.toList();
			return new Resource(protocol, paths, file.getName(), file.isDirectory());
		}

		public URI getUri() {
			if (uri == null) {
				uri = URI.create(path);
			}
			return uri;
		}

		public URI uri() {
			return getUri();
		}

		public URL getUrl() throws MalformedURLException {
			if (url == null) {
				url = getUri().toURL();
			}
			return url;
		}

		public URL url() throws MalformedURLException {
			return getUrl();
		}

		public File file() {
			return new File(getUri());
		}

		public InputStream stream() throws IOException {
			return getUrl().openStream();
		}

		@Override
		public int hashCode() {
			return path.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Resource r && Objects.equals(r.path, path);
		}

	}

}
