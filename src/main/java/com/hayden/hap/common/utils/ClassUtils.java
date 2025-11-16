package com.hayden.hap.common.utils;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
 
public class ClassUtils {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ClassUtils.class);
	
	private static final String PACKAGE_TO_SCAN = "com.hayden.hap";
	
	/**
	 * 根据接口找同包下的实现类
	 * @param c
	 * @return 
	 * @author zhangfeng
	 * @date 2015年5月29日
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<Class<? extends T>> getAllClassByInterface(Class<T> c){
        List<Class<? extends T>> returnClassList = new ArrayList<Class<? extends T>>();
         
        if(c.isInterface()){
            String packageName = c.getPackage().getName();
            try{
                List<Class<?>> allClass = listClassesInPackage(packageName);
                for(int i=0; i<allClass.size(); i++){
                    if(c.isAssignableFrom(allClass.get(i))){
                        if(!c.equals(allClass.get(i))){
                            returnClassList.add((Class<? extends T>) allClass.get(i));
                        }
                    }
                }
            }catch(ClassNotFoundException e){
            	logger.error(e.getMessage(), e);
            }catch(IOException e){
            	logger.error(e.getMessage(), e);
            }
        }
        return returnClassList;
    }
     
     
    public static List<Class<?>> listClassesInPackage(String packageName)
    		throws ClassNotFoundException, IOException {
    
    	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    	assert classLoader != null;
    	String path = packageName.replace('.', '/');
    
    	Enumeration<URL> resources = classLoader.getResources(path);
    
    	List<String> dirs = new ArrayList<String>();
    
    	while (resources.hasMoreElements()) {
    		URL resource = resources.nextElement();
    		dirs.add(URLDecoder.decode(resource.getFile(), "UTF-8"));
    	}
    
    	TreeSet<String> classes = new TreeSet<String>();
    	for (String directory : dirs) {
    		classes.addAll(findClasses(directory, packageName));
    	}
    
    	ArrayList<Class<?>> classList = new ArrayList<Class<?>>();
    	for (String clazz : classes) {
    		classList.add(Class.forName(clazz));
    	}
    
    	return classList;
    }
    
    private static TreeSet<String> findClasses(String path, String packageName) throws MalformedURLException, IOException {
    
    	TreeSet<String> classes = new TreeSet<String>();
    
    	if (path.startsWith("file:") && path.contains("!")) {
    		String[] split = path.split("!");
    		URL jar = new URL(split[0]);
    		ZipInputStream zip = new ZipInputStream(jar.openStream());
    		ZipEntry entry;
    		while ((entry = zip.getNextEntry()) != null) {
    			if (entry.getName().endsWith(".class")) {
    				String className = entry.getName().replaceAll("[$].*", "").replaceAll("[.]class", "").replace('/', '.');
    				if (className.startsWith(packageName)) {
    					classes.add(className);
    				}
    			}
    		}
    	}
    
    	File dir = new File(path);
    	if (!dir.exists()) {
    		return classes;
    	}
    	File[] files = dir.listFiles();
    	for (File file : files) {
    		if (file.isDirectory()) {
    			assert !file.getName().contains(".");
    			classes.addAll(findClasses(file.getAbsolutePath(), packageName + "." + file.getName()));
    		} else if (file.getName().endsWith(".class")) {
    			String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
    
    			classes.add(className);
    		}
    	}
    	return classes;
    }

    /**
     * 根据接口获取实现类，不限定同包
     * @param interfaceClass
     * @return 
     * @author zhangfeng
     * @date 2018年8月23日
     */
    public static <T> Set<Class<? extends T>> getImplClassByItf(Class<T> interfaceClass) {
    	Collection<URL> set = ClasspathHelper.forPackage(PACKAGE_TO_SCAN);
		Reflections reflections = new Reflections(new ConfigurationBuilder().addUrls(set).setScanners(new SubTypesScanner()));

		Set<Class<? extends T>> subTypesOf = reflections.getSubTypesOf(interfaceClass);
		return subTypesOf;
    }

	/**
	 * 根据注解查找类
	 * @param annotationClass
	 * @return
	 * @author zhangfeng
	 * @date 2020年04月13日
	 */
	public static Set<Class<?>> getClassByAnnotation(Class annotationClass) {
		Collection<URL> set = ClasspathHelper.forPackage(PACKAGE_TO_SCAN);
		Reflections reflections = new Reflections(new ConfigurationBuilder().addUrls(set).setScanners(new TypeAnnotationsScanner(),new SubTypesScanner()));

		Set<Class<?>> subTypesOf = reflections.getTypesAnnotatedWith(annotationClass);
		return subTypesOf;
	}
}

