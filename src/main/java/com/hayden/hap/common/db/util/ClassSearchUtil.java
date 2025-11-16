package com.hayden.hap.common.db.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ClassName: ClassSearchUtil
 * @Description: 通过匹配规则搜索位于某个package下的class，目前支持按父类型和注解搜索
 * @author LUYANYING
 * @date 2015年4月17日 下午7:39:21
 * @version V1.0
 * 
 */
public class ClassSearchUtil<T> {
	private static final Logger logger = LoggerFactory.getLogger(ClassSearchUtil.class);

	private Set<Class<? extends T>> matches = new HashSet<Class<? extends T>>();

	private ClassLoader classloader;

	public Set<Class<? extends T>> getClasses() {
		return matches;
	}

	public ClassLoader getClassLoader() {
		return classloader == null ? Thread.currentThread().getContextClassLoader() : classloader;
	}

	public void setClassLoader(ClassLoader classloader) {
		this.classloader = classloader;
	}

	public ClassSearchUtil<T> findImplementations(Class<?> parent, String... packageNames) {
		if (packageNames == null)
			return this;

		IClassMatcher classMatcher = new IsAClassMatcher(parent);
		for (String pkg : packageNames) {
			find(classMatcher, pkg);
		}

		return this;
	}

	public ClassSearchUtil<T> findAnnotated(Class<? extends Annotation> annotation, String... packageNames) {
		if (packageNames == null)
			return this;

		IClassMatcher classMatcher = new AnnotatedWithClassMatcher(annotation);
		for (String pkg : packageNames) {
			find(classMatcher, pkg);
		}

		return this;
	}

	public ClassSearchUtil<T> find(IClassMatcher classMatcher, String packageName) {
		String path = getPackagePath(packageName);

		try {
			DefaultResourceSearcher defaultResourceSearcher = new DefaultResourceSearcher();
			List<String> children = defaultResourceSearcher.list(path);
			for (String child : children) {
				if (child.endsWith(".class"))
					addIfMatching(classMatcher, child);
			}
		} catch (IOException ioe) {
			logger.error("Could not read package: " + packageName, ioe);
		}

		return this;
	}

	protected String getPackagePath(String packageName) {
		return packageName == null ? null : packageName.replace('.', '/');
	}

	@SuppressWarnings("unchecked")
	protected void addIfMatching(IClassMatcher classMatcher, String fqn) {
		try {
			String externalName = fqn.substring(0, fqn.indexOf('.')).replace('/', '.');
			ClassLoader loader = getClassLoader();
			logger.debug("Checking to see if class " + externalName + " matches criteria [" + classMatcher + "]");

			Class<?> type = loader.loadClass(externalName);
			if (classMatcher.matches(type)) {
				matches.add((Class<T>) type);
			}
		} catch (Throwable t) {
			logger.warn("Could not examine class '" + fqn + "'" + " due to a " + t.getClass().getName() + " with message: " + t.getMessage());
		}
	}

	
	public static interface IClassMatcher {
		boolean matches(Class<?> type);
	}

	public static class IsAClassMatcher implements IClassMatcher {
		private Class<?> parent;

		public IsAClassMatcher(Class<?> parentType) {
			this.parent = parentType;
		}

		public boolean matches(Class<?> type) {
			return type != null && parent.isAssignableFrom(type);
		}

		@Override
		public String toString() {
			return "is assignable to " + parent.getSimpleName();
		}
	}

	public static class AnnotatedWithClassMatcher implements IClassMatcher {
		private Class<? extends Annotation> annotation;

		public AnnotatedWithClassMatcher(Class<? extends Annotation> annotation) {
			this.annotation = annotation;
		}

		public boolean matches(Class<?> type) {
			return type != null && type.isAnnotationPresent(annotation);
		}

		@Override
		public String toString() {
			return "annotated with @" + annotation.getSimpleName();
		}
	}
}
