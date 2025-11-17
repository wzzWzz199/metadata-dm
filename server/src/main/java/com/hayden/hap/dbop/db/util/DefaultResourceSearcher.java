package com.hayden.hap.dbop.db.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * @ClassName: DefaultResourceSearcher
 * @Description: 资源定位器 不同应用服务器会有不同定位方式 目前只支持默认的 适用大部分应用服务器
 * @author LUYANYING
 * @date 2015年4月17日 下午7:58:35
 * @version V1.0
 * 
 */
public class DefaultResourceSearcher {
	private static final Logger logger = LoggerFactory.getLogger(DefaultResourceSearcher.class);

	private static final byte[] JAR_MAGIC = { 'P', 'K', 3, 4 };

	protected static List<URL> getResources(String path) throws IOException {
		return Collections.list(Thread.currentThread().getContextClassLoader().getResources(path));
	}

	public List<String> list(String path) throws IOException {
		List<String> names = new ArrayList<String>();
		for (URL url : getResources(path)) {
			names.addAll(list(url, path));
		}
		return names;
	}

	public List<String> list(URL url, String path) throws IOException {
		InputStream is = null;
		try {
			List<String> resources = new ArrayList<String>();

			URL jarUrl = findJarForResource(url);
			if (jarUrl != null) {
				is = jarUrl.openStream();
				logger.debug("Listing " + url);
				resources = listResources(new JarInputStream(is), path);
			} else {
				List<String> children = new ArrayList<String>();
				try {
					if (isJar(url)) {
						is = url.openStream();
						JarInputStream jarInput = new JarInputStream(is);
						logger.debug("Listing " + url);
						for (JarEntry entry; (entry = jarInput.getNextJarEntry()) != null;) {
							logger.debug("Jar entry: " + entry.getName());
							children.add(entry.getName());
						}
					} else {
						is = url.openStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(is));
						List<String> lines = new ArrayList<String>();
						for (String line; (line = reader.readLine()) != null;) {
							logger.debug("Reader entry: " + line);
							lines.add(line);
							if (getResources(path + "/" + line).isEmpty()) {
								lines.clear();
								break;
							}
						}

						if (!lines.isEmpty()) {
							logger.debug("Listing " + url);
							children.addAll(lines);
						}
					}
				} catch (FileNotFoundException e) {
					if ("file".equals(url.getProtocol())) {
						File file = new File(url.getFile());
						logger.debug("Listing directory " + file.getAbsolutePath());
						if (file.isDirectory()) {
							logger.debug("Listing " + url);
							children = Arrays.asList(file.list());
						}
					} else {
						// No idea where the exception came from so rethrow it
						throw e;
					}
				}

				String prefix = url.toExternalForm();
				if (!prefix.endsWith("/"))
					prefix = prefix + "/";

				for (String child : children) {
					String resourcePath = path + "/" + child;
					resources.add(resourcePath);
					URL childUrl = new URL(prefix + child);
					resources.addAll(list(childUrl, resourcePath));
				}
			}

			return resources;
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception e) {
			}
		}
	}

	protected List<String> listResources(JarInputStream jar, String path) throws IOException {
		if (!path.startsWith("/"))
			path = "/" + path;
		if (!path.endsWith("/"))
			path = path + "/";

		List<String> resources = new ArrayList<String>();
		for (JarEntry entry; (entry = jar.getNextJarEntry()) != null;) {
			if (!entry.isDirectory()) {
				// Add leading slash if it's missing
				String name = entry.getName();
				if (!name.startsWith("/"))
					name = "/" + name;

				// Check file name
				if (name.startsWith(path)) {
					logger.debug("Found resource: " + name);
					resources.add(name.substring(1)); // Trim leading slash
				}
			}
		}
		return resources;
	}

	protected URL findJarForResource(URL url) throws MalformedURLException {
		logger.debug("Find JAR URL: " + url);

		try {
			for (;;) {
				url = new URL(url.getFile());
				logger.debug("Inner URL: " + url);
			}
		} catch (MalformedURLException e) {
		}

		StringBuilder jarUrl = new StringBuilder(url.toExternalForm());
		int index = jarUrl.lastIndexOf(".jar");
		if (index >= 0) {
			jarUrl.setLength(index + 4);
			logger.debug("Extracted JAR URL: " + jarUrl);
		} else {
			logger.debug("Not a JAR: " + jarUrl);
			return null;
		}

		try {
			URL testUrl = new URL(jarUrl.toString());
			if (isJar(testUrl)) {
				return testUrl;
			} else {
				logger.debug("Not a JAR: " + jarUrl);
				jarUrl.replace(0, jarUrl.length(), testUrl.getFile());
				File file = new File(jarUrl.toString());

				// File name might be URL-encoded
				if (!file.exists()) {
					try {
						file = new File(URLEncoder.encode(jarUrl.toString(), "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						throw new RuntimeException("Unsupported encoding?  UTF-8?  That's unpossible.");
					}
				}

				if (file.exists()) {
					logger.debug("Trying real file: " + file.getAbsolutePath());
					testUrl = file.toURI().toURL();
					if (isJar(testUrl)) {
						return testUrl;
					}
				}
			}
		} catch (MalformedURLException e) {
			logger.warn("Invalid JAR URL: " + jarUrl);
		}

		logger.debug("Not a JAR: " + jarUrl);
		return null;
	}

	protected String getPackagePath(String packageName) {
		return packageName == null ? null : packageName.replace('.', '/');
	}

	protected boolean isJar(URL url) {
		return isJar(url, new byte[JAR_MAGIC.length]);
	}

	protected boolean isJar(URL url, byte[] buffer) {
		InputStream is = null;
		try {
			is = url.openStream();
			is.read(buffer, 0, JAR_MAGIC.length);
			if (Arrays.equals(buffer, JAR_MAGIC)) {
				logger.debug("Found JAR: " + url);
				return true;
			}
		} catch (Exception e) {
			// Failure to read the stream means this is not a JAR
		} finally {
			try {
				is.close();
			} catch (Exception e) {
			}
		}

		return false;
	}
}
